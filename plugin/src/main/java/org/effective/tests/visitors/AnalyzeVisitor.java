package org.effective.tests.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithExpression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.Visitable;
import org.effective.tests.effects.*;
import org.effective.tests.modifier.NodeVisitor;
import org.effective.tests.staticVariables.VarClassField;
import org.effective.tests.staticVariables.VarMethodReturn;
import org.effective.tests.staticVariables.VarType;

import java.util.*;

public class AnalyzeVisitor extends NodeVisitor<AnalyzeContext> {
    AnalyzeContext ctx;
    String targetClass;
    private final List<String> supportedAssertMethods = new ArrayList<>(List.of(
            "assertEquals",
            "assertFalse",
            "assertTrue",
            "assertNotNull",
            "assertNull",
            "assertSame",
            "assertTrue"
    ));

    public AnalyzeVisitor(EffectContext effectContext) {
        super();
        ctx = new AnalyzeContext(effectContext);
    }

    public AnalyzeContext getContext() {
        return ctx;
    }

    public CompilationUnit visit(CompilationUnit cu, AnalyzeContext analyzeContext) {
        super.visit(cu, analyzeContext);
        ImportDeclaration newImport = new ImportDeclaration("org.effective.tests.EffectsAnalyzer", false, false);
        cu.addImport(newImport);
        return cu;
    }

    // returns map of used methods and only the fields and return values that have been asserted for it
    public Map<MethodData, Set<VarType>> analyzeTest(Node n, String targetClass) {
        this.targetClass = targetClass;
        n.accept(this, ctx);
        Map<MethodData, Set<VarType>> methodFieldCoverage = ctx.usedMethodsAndCoverage;
        return methodFieldCoverage;
    }

    @Override
    public ObjectCreationExpr visit(final ObjectCreationExpr oce, final AnalyzeContext analyzeContext) {
        if (oce.getTypeAsString().equals(targetClass)) {
            Node node = oce.getParentNode().orElse(null);
            if (node instanceof VariableDeclarator vd) {
                analyzeContext.classInstances.put(vd.getNameAsString(), new HashMap<>());
            } else if (node instanceof AssignExpr ae) { // case where it assigns to an existing variable
                String targetName = ae.getTarget().toString();
                if (!analyzeContext.classInstances.containsKey(targetName)) {
                    analyzeContext.classInstances.put(targetName, new HashMap<>());
                }
            }
        }
        return oce;
    }

    @Override
    public IfStmt visit(final IfStmt is, final AnalyzeContext analyzeContext) {
        Statement then = is.getThenStmt();
        Statement other = is.getElseStmt().orElse(null);
        if (other == null) {
            return null;
        }
        AnalyzeContext ctx1 = analyzeContext.copy();
        AnalyzeContext ctx2 = analyzeContext.copy();
        then.accept(this, ctx1); // analyze each branch separately
        other.accept(this, ctx2);
        ctx1.intersect(ctx2); // control flow intersect the analysis contexts
        analyzeContext.union(ctx1); // union back with main flow
        return is;
    }

    @Override
    public Visitable visit(BlockStmt n, AnalyzeContext arg) {
        // create a new context for the new scope
        AnalyzeContext newScopeContext = arg.copy();
        // visit the node
        Visitable next = super.visit(n, newScopeContext);

        // take the results not relevant to the scope from the child scope
        arg.unscopedUnion(newScopeContext);

        return next;
    }

    // this override is to handle general assignments (i = 3, s = "string")
    @Override
    public AssignExpr visit(final AssignExpr ae, final AnalyzeContext analyzeContext) {
        String targetName = ae.getTarget().toString();
        if (analyzeContext.variableInstances.containsKey(targetName)) {
            if (!(ae.getValue() instanceof FieldAccessExpr)) { // if assignment isn't a field access then remove it (i = 3)
                analyzeContext.variableInstances.remove(targetName);
            }
        }
        super.visit(ae, analyzeContext);
        return ae;
    }

    @Override
    public FieldAccessExpr visit(final FieldAccessExpr fae, final AnalyzeContext analyzeContext) {
        String classInstance = fae.getScope().toString();
        String field = fae.getNameAsString();
        if (analyzeContext.classInstances.containsKey(classInstance)) {
            Node node = fae.getParentNode().orElse(null);
            if (node instanceof VariableDeclarator vd) {
                analyzeContext.variableInstances.put(vd.getNameAsString(), new VarClassField(classInstance, field));
            } else if (node instanceof AssignExpr ae) { // case where it assigns an existing variable
                String targetName = ae.getTarget().toString();
                Expression fieldScope = fae.getScope(); // if it's direct assignment to class field (class.field = 3)
                if (fieldScope instanceof NameExpr ne && analyzeContext.classInstances.containsKey(ne.toString())) {
                    analyzeContext.classInstances.get(ne.toString()).put(new Field(fae.getNameAsString()), new DirectMod());
                    analyzeContext.usedMethodsAndCoverage.put(new DirectMod(), new HashSet<>());
                } else {
                    analyzeContext.variableInstances.put(targetName, new VarClassField(classInstance, field));
                }
            }
        }
        return fae;
    }

    @Override
    public MethodCallExpr visit(final MethodCallExpr mce, final AnalyzeContext analyzeContext) {
        Expression scope = mce.getScope().orElse(null);

        if (scope != null) {
            String classInstance = scope.toString();

            if (analyzeContext.classInstances.containsKey(classInstance)) { // check if the scope is to a class instance
                String methodName = mce.getNameAsString();
                List<String> methodParameterTypes = new ArrayList<>();
                mce.getArguments().forEach(arg -> {
                    methodParameterTypes.add(arg.calculateResolvedType().describe());
                });
                // handle adding the method's effects to the class instance
                MethodData method = new MethodData(methodName, methodParameterTypes, 0);

                if (!analyzeContext.usedMethodsAndCoverage.containsKey(method)) {
                    analyzeContext.usedMethodsAndCoverage.put(method, new HashSet<>()); // add the method to used methods
                }

                List<Effect> effects = analyzeContext.classContext.getMethodEffects(methodName, methodParameterTypes);
                List<Effect> testableEffects = effects.stream().filter(Effect::isTestable).toList();
                Map<Field, MethodData> currentFieldsEffected = analyzeContext.classInstances.get(classInstance);

                testableEffects.forEach(e -> {
                    if (e instanceof Modification m) {
                        currentFieldsEffected.put(new Field(m.getField().toString()), method);
                    }
                });

                // begin checking if method returns a value (getter or return)
                Node parent = mce.getParentNode().orElse(null);
                if (parent instanceof VariableDeclarator vd) {
                    String variableName = vd.getNameAsString();
                    for (Effect e : testableEffects) { // since there will always only be 1 return value, we can just scan through the list
                        if (e instanceof Getter g) {
                            analyzeContext.variableInstances.put(variableName, new VarClassField(classInstance, g.getFieldName()));
                        } else if (e instanceof Return) {
                            analyzeContext.variableInstances.put(variableName, new VarMethodReturn(classInstance, method));
                        }
                    }
                } else if (parent instanceof AssignExpr ae) {
                    String targetName = ae.getTarget().toString();
                    for (Effect e : testableEffects) {
                        if (e instanceof Getter g) {
                            analyzeContext.variableInstances.put(targetName, new VarClassField(classInstance, g.getFieldName()));
                        } else if (e instanceof Return) {
                            analyzeContext.variableInstances.put(targetName, new VarMethodReturn(classInstance, method));
                        }
                    }
                }
            }
        } else if (supportedAssertMethods.contains(mce.getNameAsString())) { // handling assertions
            mce.getArguments().forEach(arg -> {
                visitAssertArgument(mce, analyzeContext, arg);
            });
        }

        return mce;
    }

    private void visitAssertArgument(MethodCallExpr mce, AnalyzeContext analyzeContext, Expression arg) {
        // first: reduce any compound expressions to the classes we are interested in
        if (arg instanceof AssignExpr ae) {
            visitAssertArgument(mce, analyzeContext, ae.getValue());
        }

        if (arg instanceof BinaryExpr be) {
            visitAssertArgument(mce, analyzeContext, be.getLeft());
            visitAssertArgument(mce, analyzeContext, be.getRight());
        }

        if (arg instanceof NodeWithExpression<?> nwe) {
            visitAssertArgument(mce, analyzeContext, nwe.getExpression());
        }

        // now: check expressions for asserts
        if (arg instanceof NameExpr ne) { // arg is a variable
            if (analyzeContext.variableInstances.containsKey((ne.getNameAsString()))) { // variable is one of the class variables
                VarType val = analyzeContext.variableInstances.get(ne.getNameAsString());

                if (val instanceof VarClassField vf) {
                    String classInstance = vf.classInstance;
                    Field field = new Field(vf.fieldName);
                    Map<Field, MethodData> effects = analyzeContext.classInstances.get(classInstance);
                    MethodData md = effects.get(field); // what method caused the modification to the field

                    if (md != null) { // for accessing a field that hasnt been affected by a method
                        analyzeContext.addMethodCoverage(md, new VarClassField(classInstance, vf.fieldName));
                        injectAssertFor(mce, classInstance, field.toString());
                    }
                } else if (val instanceof VarMethodReturn vmr) {
                    analyzeContext.addMethodCoverage(vmr.method, vmr);
                    injectAssertFor(mce, vmr.classInstance, vmr.method.methodName);
                }
            }
        } else if (arg instanceof FieldAccessExpr fae) { // arg is a class field
            String classInstance = fae.getScope().toString();
            Field field = new Field(fae.getNameAsString());

            if (analyzeContext.classInstances.containsKey(classInstance)) {
                Map<Field, MethodData> effects = analyzeContext.classInstances.get(classInstance);
                MethodData md = effects.get(field);

                analyzeContext.addMethodCoverage(md, new VarClassField(classInstance, fae.getNameAsString()));
                injectAssertFor(mce, classInstance, field.toString());
            }
        } else if (arg instanceof MethodCallExpr mceArg) { // argument is a method call
            mceArg.accept(this, analyzeContext); // to get any of the effects of the method
            Expression argScope = mceArg.getScope().orElse(null);

            if (argScope != null) {
                String classInstance = argScope.toString();

                if (analyzeContext.classInstances.containsKey(classInstance)) { // check if scope is to a class instance
                    String methodName = mceArg.getNameAsString();
                    List<String> methodParameterTypes = new ArrayList<>();
                    mceArg.getArguments().forEach(arg2 -> {
                        methodParameterTypes.add(arg.calculateResolvedType().describe());
                    });
                    MethodData method = new MethodData(methodName, methodParameterTypes, 0);

                    if (!analyzeContext.usedMethodsAndCoverage.containsKey(method)) {
                        analyzeContext.usedMethodsAndCoverage.put(method, new HashSet<>()); // add the method to used methods
                    }

                    List<Effect> effects = analyzeContext.classContext.getMethodEffects(methodName, methodParameterTypes);
                    List<Effect> testableEffects = effects.stream().filter(Effect::isTestable).toList();

                    for (Effect e : testableEffects) {
                        if (e instanceof Getter g) {
                            // we need to have it act like accessing the class field
                            Map<Field, MethodData> mceEffects = analyzeContext.classInstances.get(classInstance);
                            String field = g.getFieldName();
                            MethodData fieldEffectedMethod = mceEffects.get(new Field(field));

                            analyzeContext.addMethodCoverage(fieldEffectedMethod, new VarClassField(classInstance, field));
                            analyzeContext.addMethodCoverage(method, new VarMethodReturn(classInstance, method));

                            injectAssertFor(mce, classInstance, field);
                        } else if (e instanceof Return) {
                            analyzeContext.addMethodCoverage(method, new VarMethodReturn(classInstance, method));
                            injectAssertFor(mce, classInstance, methodName);
                        }
                    }
                }
            }
        }
    }

    private void injectAssertFor(MethodCallExpr mce, String classInstance, String effect) {
        Statement addedStatement = new ExpressionStmt(new NameExpr("EffectsAnalyzer.getInstance("+ classInstance + ").registerAssert(\"" + effect + "\")"));
        ExpressionStmt expression = getParent(mce, ExpressionStmt.class);
        BlockStmt parentBlock = getParent(mce, BlockStmt.class);
        NodeList<Statement> statements = parentBlock.getStatements();
        Optional<Range> methodRange1 = expression.getRange();
        for (int i = 0; i < statements.size(); i++) {
            Optional<Range> methodRange2 = statements.get(i).getRange();
            if (methodRange1.equals(methodRange2)) {
                parentBlock.addStatement(i + 1, addedStatement);
                break;
            }
        }

    }

}
