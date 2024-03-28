package org.effective.tests.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.effective.tests.effects.Field;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A JavaParser AST visitor that collects all fields of a given class.
 * All fields are collected; the public availability of a field is indicated as a property of that field.
 */

public class VarCollector extends NodeVisitor<VarContext> {

    public VarCollector() {
        super();
    }

    // Two useful points of entry with field declaration children
    public VarContext collectVars(CompilationUnit cu) {
        VarContext vars = new VarContext();
        cu.accept(this, vars);
        return vars;
    }

    public VarContext collectVars(ClassOrInterfaceDeclaration cd) {
        VarContext vars = new VarContext();
        cd.accept(this, vars);
        return vars;
    }

    @Override
    public void visit(final VariableDeclarationExpr vd, final VarContext vars) {
        MethodDeclaration method = getParent(vd, MethodDeclaration.class);
        String methodName = method.getNameAsString();
        int methodLine = method.getBegin().get().line;
        List<String> variableNames = vd.getVariables().stream().map(v -> v.getNameAsString()).collect(Collectors.toList());
        vars.addLocalVariables(methodName, methodLine, variableNames);
    }

    @Override
    public void visit(final FieldDeclaration fd, final VarContext vars) {
        List<Modifier> modifiers = fd.getModifiers();
        for ( VariableDeclarator v : fd.getVariables() ) {
            Field f = new Field(v.getNameAsString());
            if (modifiers.contains(Modifier.publicModifier())) {
                f.setAvailability(true);
            }
            vars.addField(f);
        }
    }

    // Visit all return statements to find getters
    @Override
    public void visit(final ReturnStmt rs, final VarContext vars) {
        BlockStmt block = getParent(rs, BlockStmt.class);
        MethodDeclaration method = getParent(block, MethodDeclaration.class);

        if (method == null) {
            throw new IllegalStateException("Return statement should be within a method");
        }

        Set<Field> fields = vars.getFields();
        String fieldName = isGetter(method, vars);
        Field f = getField(fields, fieldName);
        if (f != null) {
            f.setAvailability(true);
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

}
