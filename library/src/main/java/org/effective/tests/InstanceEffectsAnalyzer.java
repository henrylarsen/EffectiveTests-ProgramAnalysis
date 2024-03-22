package org.effective.tests;

import org.effective.tests.effectstate.EffectStateMachine;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InstanceEffectsAnalyzer<T> {
    private final Map<String, EffectStateMachine> sideEffectMachines;

    public InstanceEffectsAnalyzer(EffectsAnalyzer<T> parent) {
        sideEffectMachines = parent.getPossibleEffects()
                .stream()
                // strip the effect:line format to just effect
                .map(s -> s.substring(0, s.indexOf(":")))
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(), // key is the effect name
                        (effect) -> new EffectStateMachine(
                                effect,
                                parent::addWarning,
                                parent::addTestedEffect
                        )
                ));
    }

    // shorthand for mutation + read
    public void registerReturn(String sideEffect, int lineNum) {
        registerMutation(sideEffect, lineNum);
        registerRead(sideEffect);
    }

    public void registerMutation(String sideEffect, int lineNum) {
        sideEffectMachines.get(sideEffect).registerMutation(lineNum);
    }

    public void registerRead(String sideEffect) {
        sideEffectMachines.get(sideEffect).registerRead();
    }

    public void registerAssert(String sideEffect) {
        sideEffectMachines.get(sideEffect).registerAssert();
    }

}
