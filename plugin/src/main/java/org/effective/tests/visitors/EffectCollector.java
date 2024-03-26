package org.effective.tests.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Map<Pair<String, Integer>, List<Effect>> collectEffects(Node n, final VarContext vars) {
        ctx = new EffectContext(vars);
        n.accept(this, ctx);
        return ctx.getEffectMap();
    }

    @Override
    public void visit(final ReturnStmt rs, final EffectContext ctx) {
        MethodDeclaration method = getParent(rs, MethodDeclaration.class);
        if (method == null) {
            throw new IllegalStateException("Return statement should be within a method");
        }

        String methodName = method.getNameAsString();
        int methodLine = method.getBegin().get().line;
        Expression exp = rs.getExpression().orElse(null);

        // Return statements with no value should not be registered as effects
        if (exp == null) {
            return;
        }

        Effect e = new Return(methodName, rs.getBegin().get().line);
        ctx.addEffect(methodName, methodLine, e);
    }

    @Override
    public void visit(final AssignExpr a, final EffectContext ctx) {
        MethodDeclaration method = getParent(a, MethodDeclaration.class);

        // if an assignment is not within a method, it is in the constructor and we ignore it
        if (method == null) {
            return;
        }

        String methodName = method.getNameAsString();
        int methodLine = method.getBegin().get().line;
        String fieldName = a.getTarget().toString();
        Field f = ctx.getField(fieldName);

        if (f != null && !ctx.isLocalVariable(methodName, methodLine, fieldName)) {
            Effect e = new Modification(methodName, a.getBegin().get().line, f);
            ctx.addEffect(methodName, methodLine, e);
        }
    }

    public List<Effect> getAllEffects() {
        return ctx.getAllEffects();
    }

    public List<Effect> getAllTestableEffects() {
        return ctx.getAllTestableEffects();
    }

    /**
     * @return The collector's EffectContext.
     * <b>Note:</b> should not be used as a direct API. Effects are accessible through other methods.
     */
    public EffectContext getCtx() {
        return ctx;
    }

}
