package org.effective.tests.modifier;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Getter;
import org.effective.tests.effects.MethodData;
import org.effective.tests.effects.Modification;

import java.util.*;

/**
 * Visitor for initial class instrumentation.
 * Takes in effectsMap from EffectCollector object.
 */

public class EffectInjectionModifier extends NodeVisitor<Void> {
    private final Map<MethodData, List<Effect>> effectsMap;

    public EffectInjectionModifier(Map<MethodData, List<Effect>> effectsMap) {
        super();
        this.effectsMap = effectsMap;
    }

    // Injects import statement
    public CompilationUnit visit(CompilationUnit cu, Void arg) {
        super.visit(cu, arg);
        ImportDeclaration newImport = new ImportDeclaration("org.effective.tests.EffectsAnalyzer", false, false);
        cu.addImport(newImport);
        return cu;

    }

    // Injects EffectsAnalyzer initializer
    @Override
    public BodyDeclaration<?> visit(ClassOrInterfaceDeclaration classDeclaration, Void arg) {
        super.visit(classDeclaration, arg);

        BlockStmt staticInitializerBlock = new BlockStmt();
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setName("EffectsAnalyzer.setupAnalyzer");
        methodCallExpr.setScope(null);
        NodeList<Expression> argList = NodeList.nodeList();
        argList.add(new NameExpr(classDeclaration.getNameAsString() + ".class"));


        Set<MethodData> keys = effectsMap.keySet();
        for (MethodData key: keys) {
            List<Effect> effects = effectsMap.get(key);
            for (Effect e: effects) {
                String name;
                if (e.getClass() == Modification.class) {
                    name = ((Modification) e).getField().getName();
                } else if (e.getClass() == Getter.class) {
                    name = ((Getter) e).getFieldName();
                }else {
                    name = e.getMethodName();
                }
                Expression argExpr = new StringLiteralExpr(name +":"+e.getLineNumber());
                argList.add(argExpr);
            }

        }
        methodCallExpr.setArguments(argList);
        staticInitializerBlock.addStatement(new ExpressionStmt(methodCallExpr));
        InitializerDeclaration staticInitializer = new InitializerDeclaration(true, staticInitializerBlock);
        classDeclaration.getMembers().addFirst(staticInitializer);

        return classDeclaration;
    }

    // injects effects for return statements
    @Override
    public ReturnStmt visit(ReturnStmt rs, Void arg) {
        BlockStmt parentBlock = getParent(rs, BlockStmt.class);
        MethodDeclaration method = getParent(rs, MethodDeclaration.class);
        if (method == null) {
            return rs;
        }

        // get effectsMap key
        String methodName = method.getNameAsString();
        int methodLine = method.getBegin().get().line;
        List<String> paramTypes = new ArrayList<>();
        method.getParameters().forEach(param -> {
            paramTypes.add(param.getType().toString());
        });

        MethodData blockKey = new MethodData(methodName, paramTypes, methodLine);
        List<Effect> ctxList = effectsMap.get(blockKey);

        if (ctxList == null) {
            return rs;
        }

        for (Effect e: ctxList) {
            if (e.getLineNumber() == rs.getBegin().get().line) {
                Statement addedStatement;
                if (e.getClass() == Getter.class){
                    String name = ((Getter) e).getFieldName();
                    addedStatement = new ExpressionStmt(new NameExpr("EffectsAnalyzer.getInstanceAnalyzer(this).registerRead(\"" + name + "\")"));
                } else {
                    addedStatement = new ExpressionStmt(new NameExpr("EffectsAnalyzer.getInstanceAnalyzer(this).registerReturn(\"" + e.getMethodName() +"\"," + e.getLineNumber() + ")"));
                }
                parentBlock.addStatement(parentBlock.getStatements().indexOf(rs), addedStatement);
                break;
            }
        }


        return rs;
    }

    @Override
    public AssignExpr visit(AssignExpr a, Void arg) {
        BlockStmt parentBlock = getParent(a, BlockStmt.class);
        MethodDeclaration method = getParent(parentBlock, MethodDeclaration.class);

        if (method == null) {
            return a;
        }

        // Get key for Map
        String methodName = method.getNameAsString();
        int methodLine = method.getBegin().get().line;
        List<String> paramTypes = new ArrayList<>();
        method.getParameters().forEach(param -> {
            paramTypes.add(param.getType().toString());
        });

        MethodData blockKey = new MethodData(methodName, paramTypes, methodLine);
        List<Effect> ctxList = effectsMap.get(blockKey);

        if (ctxList == null) {
            return a;
        }

        for (Effect e: ctxList) {
            if (e.getLineNumber() == a.getBegin().get().line) {
                if (e.getClass() == Modification.class) {
                    Statement addedStatement = new ExpressionStmt(new NameExpr("EffectsAnalyzer.getInstanceAnalyzer" +
                            "(this).registerMutation(\"" + ((Modification) e).getField().getName() + "\", " + e.getLineNumber() + ")"));
                    parentBlock.addStatement((parentBlock.getEnd().get().line - e.getLineNumber()), addedStatement);
                    break;
                }
            }
        }
        return a;
    }

}
