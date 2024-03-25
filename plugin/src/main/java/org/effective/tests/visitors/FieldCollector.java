package org.effective.tests.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Field;
import org.effective.tests.effects.Return;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldCollector extends NodeVisitor<Set<Field>> {
    private Set<Field> fields;

    public FieldCollector() {
        super();
        fields = new HashSet();
    }
    public void collectFields(CompilationUnit cu) {
        cu.accept(this, fields);
    }

    public Set<Field> getFields() {
        return fields;
    }

    @Override
    public void visit(final FieldDeclaration fd, final Set<Field> fields) {
        List<Modifier> modifiers = fd.getModifiers();
        for ( VariableDeclarator v : fd.getVariables() ) {
            Field f = new Field(v.getNameAsString());
            if (modifiers.contains(Modifier.publicModifier())) {
                f.setAvailability(true);
            }
            fields.add(f);
        }
    }

    // TODO: update to check getter conditions
    @Override
    public void visit(final ReturnStmt rs, final ProgramContext ctx) {
        BlockStmt block = getParent(rs, BlockStmt.class);
        MethodDeclaration method = getParent(block, MethodDeclaration.class);

        if (method == null) {
            throw new IllegalStateException("Return statement should be within a method");
        }

        Expression exp = rs.getExpression().orElse(null);

        if (exp instanceof NameExpr) {
            String fieldName = exp.asNameExpr().getNameAsString();
            Field f = ctx.getField(fieldName);
            if (f != null) {
                f.setAvailability(true);
            }
        }
    }

}
