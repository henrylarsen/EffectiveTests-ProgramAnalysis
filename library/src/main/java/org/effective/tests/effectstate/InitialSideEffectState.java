package org.effective.tests.effectstate;

public class InitialSideEffectState extends SideEffectState {
    public InitialSideEffectState() {
    }

    @Override
    public SideEffectState handleMutation() {
        return new MutatedSideEffectState();
    }

    @Override
    public SideEffectState handleRead() {
        return this; // TODO: is this OK?
    }

    @Override
    public SideEffectState handleAssert() {
        return this; // TODO: is this OK?
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
