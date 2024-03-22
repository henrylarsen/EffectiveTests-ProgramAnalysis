package org.effective.tests.effectstate;

public class ReadSideEffectState extends SideEffectState {
    public ReadSideEffectState() {
    }

    public ReadSideEffectState(String warning) {
        super(warning);
    }

    @Override
    public SideEffectState handleRead() {
        return this;
    }

    @Override
    public SideEffectState handleMutation() {
        return new MutatedSideEffectState("Effect occurred, read, but not asserted!");
    }

    @Override
    public SideEffectState handleAssert() {
        return new AssertedSideEffectState();
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
