package org.effective.tests.effects;

import java.util.Objects;

/**
 * An instance of a value being returned, unconcerned with the value itself.
 */
public class Return extends Effect {

    public Return(String methodName, int lineNumber) {
        super(methodName, lineNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Return ret = (Return) o;
        return this.lineNumber == ret.getLineNumber() &&
                Objects.equals(this.methodName, ret.getMethodName());
    }
}
