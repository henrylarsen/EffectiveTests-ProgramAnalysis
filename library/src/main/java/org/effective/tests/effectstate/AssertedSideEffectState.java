package org.effective.tests.effectstate;

public class AssertedSideEffectState extends SideEffectState {
    public AssertedSideEffectState() {
    }

    public AssertedSideEffectState(String warning) {
        super(warning);
    }

    @Override
    public SideEffectState handleRead() {
        return this;
    }

    @Override
    public SideEffectState handleMutation() {
        return new MutatedSideEffectState();
    }

    @Override
    public SideEffectState handleAssert() {
        return this;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
