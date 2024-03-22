package org.effective.tests.effectstate;

import java.util.function.Consumer;
import java.util.function.Function;

public class EffectStateMachine {
    private final String effectName;
    private final Consumer<String> warningListener;
    private final Consumer<String> testedEffectListener;
    private SideEffectState state;
    private int currentLine = -1;

    public EffectStateMachine(String effectName, Consumer<String> warningListener, Consumer<String> testedEffectListener) {
        this.effectName = effectName;
        this.warningListener = warningListener;
        this.testedEffectListener = testedEffectListener;
        this.state = new InitialSideEffectState();
    }

    public void registerMutation(int mutationLine) {
        handleEffectAction("Mutation @ L" + mutationLine, SideEffectState::handleMutation);
        this.currentLine = mutationLine;
    }

    public void registerRead() {
        handleEffectAction("Read", SideEffectState::handleRead);
    }

    public void registerAssert() {
        handleEffectAction("Assert", SideEffectState::handleAssert);
    }

    private void handleEffectAction(String eventName, Function<SideEffectState, SideEffectState> stateFn) {
        // advance state with action provided
        state = stateFn.apply(state);

        handleWarning(eventName);

        // if we have completed the state machine, then we can report this side effect as tested
        if (state.isComplete()) {
            testedEffectListener.accept(effectName + ":" + currentLine);
        }
    }

    private void handleWarning(String eventName) {
        String warning = state.getWarning();

        if (warning == null) {
            return;
        }

        String formattedWarning = String.format("[%s - %s] ", effectName, eventName);

        if (currentLine != -1) {
            formattedWarning += "[Prev Mutation @ L" + currentLine + "] ";
        }

        formattedWarning += warning;

        state.clearWarning();
        warningListener.accept(formattedWarning);
    }
}
