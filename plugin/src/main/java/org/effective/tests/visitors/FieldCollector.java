package org.effective.tests.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.effective.tests.effects.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A JavaParser AST visitor that collects all fields of a given class.
 * All fields are collected; the public availability of a field is indicated as a property of that field.
 */

public class FieldCollector extends NodeVisitor<Set<Field>> {

    public FieldCollector() {
        super();
    }

    // Two useful points of entry with field declaration children
    public Set<Field> collectFields(CompilationUnit cu) {
        Set<Field> fields = new HashSet();
        cu.accept(this, fields);
        return fields;
    }

    public Set<Field> collectFields(ClassOrInterfaceDeclaration cd) {
        Set<Field> fields = new HashSet();
        cd.accept(this, fields);
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

    // Visit all return statements to find getters
    @Override
    public void visit(final ReturnStmt rs, final Set<Field> fields) {
        BlockStmt block = getParent(rs, BlockStmt.class);
        MethodDeclaration method = getParent(block, MethodDeclaration.class);

        if (method == null) {
            throw new IllegalStateException("Return statement should be within a method");
        }

        Expression exp = rs.getExpression().orElse(null);

        if (exp instanceof NameExpr) {
            String fieldName = exp.asNameExpr().getNameAsString();
            Field f = getField(fields, fieldName);
            if (f != null && isGetter(method, fields)) {
                f.setAvailability(true);
            }
        }
    }

    private Field getField(Set<Field> fields, String fieldName) {
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    private boolean isGetter(MethodDeclaration method, Set<Field> fields) {
        EffectCollector ec = new EffectCollector();
        ec.collectEffects(method, fields);
        return (ec.getAllEffects().size() == 1);
    }

}
