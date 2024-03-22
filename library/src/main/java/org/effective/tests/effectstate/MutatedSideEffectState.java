package org.effective.tests.effectstate;

public class MutatedSideEffectState extends SideEffectState {
    public MutatedSideEffectState() {
    }

    public MutatedSideEffectState(String warning) {
        super(warning);
    }

    @Override
    public SideEffectState handleRead() {
        return new ReadSideEffectState();
    }

    @Override
    public SideEffectState handleMutation() {
        return this;
    }

    @Override
    public SideEffectState handleAssert() {
        warning = "Assert occurred, but value was not read!";
        return this;
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
