package org.effective.tests.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.*;

import java.util.*;

public class AnalyzeVisitor extends VoidVisitorAdapter<AnalyzeContext> {
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
    // whenever a variable is overwritten with anything else other than what it originally was, a console warning will print. the context will still keep the values
    public AnalyzeVisitor(EffectContext effectContext) {
        super();
        ctx = new AnalyzeContext(effectContext);
    }

    // returns map of asserted methods and only the fields that have been asserted for it
    public Map<MethodData, Set<Field>> analyzeTest(Node n, String targetClass) {
        this.targetClass = targetClass;
        n.accept(this, ctx);
        Map<MethodData, Set<Field>> methodFieldCoverage = ctx.usedMethodsAndCoverage;
        return methodFieldCoverage;
    }

    @Override
    public void visit(final ObjectCreationExpr oce, final AnalyzeContext analyzeContext) {
        if (oce.getTypeAsString().equals(targetClass)) {
            Node node = oce.getParentNode().orElse(null);
            if (node instanceof VariableDeclarator vd) {
                analyzeContext.classInstances.put(vd.getNameAsString().toLowerCase(), new HashMap<>());
            } else if (node instanceof AssignExpr ae) { // case where it assigns an existing variable
                String targetName = ae.getTarget().toString().toLowerCase();
                if (analyzeContext.classInstances.containsKey(targetName)) { // if it does exist, continue to handle the existing effects
                    System.err.println("Warning: " + targetName + " variable will be overwritten");
                } else { // only put when it doesnt exist
                    analyzeContext.classInstances.put(targetName, new HashMap<>());
                }
            }
        }
    }

    @Override
    public void visit(final IfStmt is, final AnalyzeContext analyzeContext) {
        Statement then = is.getThenStmt();
        Statement other = is.getElseStmt().orElse(null);
        if (other == null) {
            return; // early stop, if statement does not matter b/c intersect with nothing
        }
        AnalyzeContext ctx1 = analyzeContext.blankCopy();
        AnalyzeContext ctx2 = analyzeContext.blankCopy();
        then.accept(this, ctx1); // analyze each branch separately
        other.accept(this, ctx2);
        ctx1.intersectInstances(ctx2); // control flow intersect classInstances and field variable declarations
        analyzeContext.unionInstances(ctx1); // union back with main flow
    }

    // this override is to handle general assignments (i = 3, s = "string")
    @Override
    public void visit(final AssignExpr ae, final AnalyzeContext analyzeContext) {
        String targetName = ae.getTarget().toString().toLowerCase();
        if (analyzeContext.variableInstances.containsKey(targetName)) {
            System.err.println("Warning: " + targetName + " variable will be overwritten");
            if (!(ae.getValue() instanceof FieldAccessExpr)) { // if assignment isn't a field access then remove it (i = 3)
                analyzeContext.variableInstances.remove(targetName);
            }
        }

        super.visit(ae, analyzeContext);
    }
//
    @Override
    public void visit(final FieldAccessExpr fae, final AnalyzeContext analyzeContext) {
        String scope = fae.getScope().toString().toLowerCase();
        String field = fae.getNameAsString().toLowerCase();
        if (analyzeContext.classInstances.containsKey(scope)) {
            Node node = fae.getParentNode().orElse(null);
            if (node instanceof VariableDeclarator vd) {
                analyzeContext.variableInstances.put(vd.getNameAsString(), new Pair<>(scope, field));
            } else if (node instanceof AssignExpr ae) { // case where it assigns an existing variable
                String targetName = ae.getTarget().toString().toLowerCase();
                if (analyzeContext.variableInstances.containsKey(targetName)) {
                    System.err.println("Warning: " + targetName + " variable will be overwritten");
                }
                analyzeContext.variableInstances.put(targetName, new Pair<>(scope, field));
            }
        }
    }
//
    // TODO: handle getters to variables
    // currently going to ignore implementation of getters, now only tracking the effects of a method
    @Override
    public void visit(final MethodCallExpr mce, final AnalyzeContext analyzeContext) {
        Expression scope = mce.getScope().orElse(null);
        if (scope != null) {
            String classInstance = scope.toString();
            String methodName = mce.getNameAsString();
            List<String> methodParameterTypes = new ArrayList<>();
            mce.getArguments().forEach(arg -> {
                methodParameterTypes.add(arg.calculateResolvedType().describe());
            });
            // handle adding the method's effects to the class instance
            if (analyzeContext.classInstances.containsKey(classInstance)) { // check if the scope is to a class instance
                MethodData method = new MethodData(methodName, methodParameterTypes, 0);
                if (!analyzeContext.usedMethodsAndCoverage.containsKey(method)) {
                    analyzeContext.usedMethodsAndCoverage.put(method, new HashSet<>()); // add the method to used methods
                }
                List<Effect> effects = analyzeContext.classContext.getMethodEffects(methodName, methodParameterTypes);
                List<Effect> testableEffects = effects.stream().filter(Effect::isTestable).toList();
                Map<Field, MethodData> currentFieldsEffected = analyzeContext.classInstances.get(classInstance);
                testableEffects.forEach(e -> {
                    if (e instanceof Modification m) {
                        currentFieldsEffected.put(new Field(m.getField().toString().toLowerCase()), method);
                    }
                });
                // begin checking if method is a getter
                Node parent = mce.getParentNode().orElse(null);
                String prefix = "get";
                if (parent instanceof VariableDeclarator vd) {
                    if (methodName.startsWith(prefix)) {
                        String field = methodName.substring(prefix.length());
                        // check if the getField method has a field for it
                        if (analyzeContext.classContext.getFields().stream().anyMatch(f -> f.getName().equalsIgnoreCase(field))) {
                            analyzeContext.variableInstances.put(vd.getNameAsString(), new Pair<>(classInstance, field));
                        }
                    }
                } else if (parent instanceof AssignExpr ae) {
                    if (methodName.startsWith(prefix)) {
                        String field = methodName.substring(prefix.length());
                        String target = ae.getTarget().toString().toLowerCase();
                        // check if already in field variables
                        if (analyzeContext.variableInstances.containsKey(target)) {
                            System.err.println("Warning: " + target + " will be overwritten");
                            analyzeContext.variableInstances.remove(target);
                        }
                        if (analyzeContext.classContext.getFields().stream().anyMatch(f -> f.getName().equalsIgnoreCase(field))) {
                            analyzeContext.variableInstances.put(target, new Pair<>(classInstance, field));
                        }
                    }
                }

            }

        } else if (supportedAssertMethods.contains(mce.getNameAsString())) { // handling assertions
            mce.getArguments().forEach(arg -> {
                if (arg instanceof NameExpr ne) { // arg is a variable
                    if (analyzeContext.variableInstances.containsKey((ne.getNameAsString()))) { // variable is one of the class variables
                        Pair<String, String> val = analyzeContext.variableInstances.get(ne.getNameAsString());
                        String classInstance = val.a;
                        Field classField = new Field(val.b.toLowerCase());
                        Map<Field, MethodData> effects = analyzeContext.classInstances.get(classInstance);
                        MethodData md = effects.get(classField);
                        if (md != null) { // for accessing a field that hasnt been affected by a method
                            analyzeContext.usedMethodsAndCoverage.get(md).add(classField);
                        }

                    }
                } else if (arg instanceof FieldAccessExpr fae) { // arg is a class field
                    String classInstance = fae.getScope().toString();
                    Field field = new Field(fae.getNameAsString().toLowerCase());
                    Map<Field, MethodData> effects = analyzeContext.classInstances.get(classInstance);
                    MethodData md = effects.get(field);
                    analyzeContext.usedMethodsAndCoverage.get(md).add(field);
                } else if (arg instanceof MethodCallExpr mceArg) { // argument is a method call to a getter
                    mceArg.accept(this, analyzeContext); // to get any of the effects of the method, we just need to get the references variable for the getter below afterward
                    Expression argScope = mceArg.getScope().orElse(null);
                    if (argScope != null) {
                        String classInstance = argScope.toString().toLowerCase();
                        if (analyzeContext.classInstances.containsKey(classInstance)) {
                            String methodName = mceArg.getNameAsString();
                            String prefix = "get";
                            if (methodName.startsWith(prefix)) {
                                String fieldName = methodName.substring(prefix.length()).toLowerCase();
                                Field field = new Field(fieldName);
                                if (analyzeContext.classContext.getFields().stream().anyMatch(f -> f.getName().equalsIgnoreCase(field.getName()))) {
                                    Map<Field, MethodData> effects = analyzeContext.classInstances.get(classInstance);
                                    MethodData md = effects.get(field);
                                    if (md != null) {
                                        analyzeContext.usedMethodsAndCoverage.get(md).add(field);
                                    } // if null, that means field hasn't been modified yet and we won't do anything with it
                                }
                            }
                        }

                    }
                }
            });
        }

    }
}
