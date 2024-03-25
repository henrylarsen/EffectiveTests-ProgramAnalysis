package org.effective.tests.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.effective.tests.effects.*;

import java.util.List;
import java.util.Set;

/**
 * Collects the effects within a JavaParser AST.
 * No restrictions on a starting node are given; any node should function as an entry point.
 * Effects are stored in and accessible from the ProgramContext object.
 */
public class EffectCollector extends NodeVisitor<EffectContext> {
    EffectContext ctx;

    public EffectCollector() {
        super();
    }

    public List<Effect> collectEffects(Node n, final Set<Field> fields) {
        ctx = new EffectContext(fields);
        n.accept(this, ctx);
        return ctx.getAllTestableEffects();
    }

    @Override
    public void visit(final ReturnStmt rs, final EffectContext ctx) {
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

        Effect e = new Return(methodName, rs.getBegin().get().line);
        ctx.addEffect(block, e);
    }

    @Override
    public void visit(final AssignExpr a, final EffectContext ctx) {
        BlockStmt block = getParent(a, BlockStmt.class);
        MethodDeclaration method = getParent(block, MethodDeclaration.class);

        // if an assignment is not within a method, it is in the constructor and we ignore it
        if (method == null) {
            return;
        }

        String methodName = method.getNameAsString();
        Field f = ctx.getField(a.getTarget().toString());

        if (f != null) {
            Effect e = new Modification(methodName, a.getBegin().get().line, f);
            ctx.addEffect(block, e);
        }
    }

    public List<Effect> getAllEffects() {
        return ctx.getAllEffects();
    }

    /**
     * @return The collector's EffectContext.
     * <b>Note:</b> should not be used as a direct API. Effects are accessible through collectEffects() and getAllEffects()
     */
    public EffectContext getCtx() {
        return ctx;
    }

}
