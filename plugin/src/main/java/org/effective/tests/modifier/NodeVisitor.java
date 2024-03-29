package org.effective.tests.modifier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.ModifierVisitor;

public abstract class NodeVisitor<U> extends ModifierVisitor<U> {
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
}
