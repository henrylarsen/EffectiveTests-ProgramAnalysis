package org.effective.tests.visitors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.effective.tests.effects.*;

import java.util.List;

public class EffectsCollector extends VoidVisitorAdapter<ProgramContext> {

    public EffectsCollector() {
        super();
    }

    @Override
    public void visit(final FieldDeclaration fd, final ProgramContext ctx) {
        List<Modifier> modifiers = fd.getModifiers();
        for ( VariableDeclarator v : fd.getVariables() ) {
            Field f = new Field(v.getNameAsString());
            if (modifiers.contains(Modifier.publicModifier())) {
                f.setAvailability(true);
            }
            ctx.addField(f);
        }

    }

    @Override
    public void visit(final ReturnStmt rs, final ProgramContext ctx) {
        BlockStmt block = getParent(rs, BlockStmt.class);
        MethodDeclaration method = getParent(block, MethodDeclaration.class);
        if (method == null) {
            throw new IllegalStateException("Return statement should be within a method");
        }

        String methodName = method.getNameAsString();
        Expression exp = rs.getExpression().orElse(null);

        if (exp instanceof NameExpr) {
            String fieldName = exp.asNameExpr().getNameAsString();
            Field f = ctx.getField(fieldName);
            if (f != null) {
                f.setAvailability(true);
            }
        }

        Effect e = new Return(methodName, rs.getBegin().get().line);
        ctx.addEffect(block, e);
    }

    @Override
    public void visit(final AssignExpr a, final ProgramContext ctx) {
        BlockStmt block = getParent(a, BlockStmt.class);
        MethodDeclaration method = getParent(block, MethodDeclaration.class);

        // if an assignment is not within a method, it is in the constructor and we ignore it
        if (method == null) {
            return;
        }

        String methodName = method.getNameAsString();
        Field f = ctx.getField(a.getTarget().toString());

        // Only register modifications of publicly available fields
        if ( f != null && f.isAvailable() ) {
            Effect e = new Modification(methodName, a.getBegin().get().line, f);
            ctx.addEffect(block, e);
        }

    }

    private <T extends Node> T getParent(Node n, Class<T> parentClass) {
        if (n == null) {
            return null;
        }

        Node ancestor = n.getParentNode().orElse(null);

        if (ancestor == null) {
            return null;
        } else if (parentClass.isInstance(ancestor)) {
            return (T) ancestor;
        }
        return getParent(ancestor, parentClass);
    }

}
