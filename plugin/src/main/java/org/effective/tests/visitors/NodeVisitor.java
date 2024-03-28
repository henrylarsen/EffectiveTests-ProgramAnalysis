package org.effective.tests.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.effective.tests.effects.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class NodeVisitor<U> extends VoidVisitorAdapter<U> {
    protected <T extends Node> T getParent(Node n, Class<T> parentClass) {
        if (n == null) {
            return null;
        }
        Node ancestor = n.getParentNode().orElse(null);

        if (ancestor == null) {
            return null;
        } else if (parentClass.isInstance(ancestor)) {
            return parentClass.cast(ancestor);
        }
        return getParent(ancestor, parentClass);
    }

    protected String isGetter(MethodDeclaration m, VarContext ctx) {
        List<Node> nodesWithEffects = getEffects(m);
        if (nodesWithEffects.size() == 1) {
            Node n = nodesWithEffects.get(0);
            if (n instanceof ReturnStmt) {
                ReturnStmt ret = (ReturnStmt) n;
                Expression expr = ret.getExpression().orElse(null);
                if (expr instanceof NameExpr) {
                    String fieldName = ((NameExpr) expr).getNameAsString();
                    if (!ctx.isLocalVariable(m.getNameAsString(), getLineNumber(m), fieldName)) {
                        return ((NameExpr) expr).getNameAsString();
                    }
                }
            }
        }
        return null;
    }

    private List<Node> getEffects(Node n) {
        List<Node> nodesWithEffects = new ArrayList();
        if (n instanceof AssignExpr || n instanceof ReturnStmt) {
            nodesWithEffects.add(n);
        }
        for (Node child : n.getChildNodes()) {
            nodesWithEffects.addAll(getEffects(child));
        }
        return nodesWithEffects;
    }

    protected int getLineNumber(Node n) {
        return n.getBegin().get().line;
    }
}
