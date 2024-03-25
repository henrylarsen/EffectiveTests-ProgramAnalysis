package org.effective.tests.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.effective.tests.effects.*;

import java.util.Set;

public class EffectCollector extends NodeVisitor<ProgramContext> {

    public EffectCollector() {
        super();
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

        // Return statements with no value should not be registered as effects
        if (exp == null) {
            return;
        }

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

}
