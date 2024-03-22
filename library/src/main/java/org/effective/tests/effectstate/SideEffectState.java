package org.effective.tests.effectstate;

public abstract class SideEffectState {
    protected String warning;

    public SideEffectState() {
    }

    protected SideEffectState(String warning) {
        this.warning = warning;
    }

    public String getWarning() {
        return warning;
    }

    public void clearWarning() {
        warning = null;
    }

    public abstract SideEffectState handleRead();

    public abstract SideEffectState handleMutation();

    public abstract SideEffectState handleAssert();

    public abstract boolean isComplete();
}
