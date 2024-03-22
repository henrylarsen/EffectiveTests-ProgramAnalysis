package org.effective.tests;

import org.effective.tests.util.GCMonitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * An analyzer that tracks the side effects from class T.
 *
 * This analyzer is an aggregate for all instances of class T,
 * for information about effects tracking at an instance level,
 * see InstanceEffectsAnalyzer.
 *
 * The analyzer is initialized with all the possible effects that
 * need to be tracked. Then, instances of class T report tested
 * effects, and finally, all the "untested" effects can be found
 * from getUntestedEffects.
 *
 * @param <T>
 * @see InstanceEffectsAnalyzer
 */
public class EffectsAnalyzer<T> {
    private static final Map<Class<?>, EffectsAnalyzer<?>> effectsAnalyzers = new HashMap<>();
    // concurrent hash map used as GC monitor will remove elements from another thread
    private final Map<Integer, InstanceEffectsAnalyzer<T>> instanceAnalyzers = new ConcurrentHashMap<>();

    private final Set<String> possibleEffects;
    private final Set<String> testedEffects;
    private final Set<String> warnings;

    private EffectsAnalyzer(Set<String> possibleEffects) {
        this.possibleEffects = possibleEffects;
        this.testedEffects = new HashSet<>();
        this.warnings = new HashSet<>();

        // add a listener to the GC monitor to remove effects analyzers when the instance
        // is finalized from the GC
        GCMonitor.get().addListener(instanceAnalyzers::remove);
    }

    public static <T> InstanceEffectsAnalyzer<T> getInstance(T instance) {
        EffectsAnalyzer<T> analyzer = (EffectsAnalyzer<T>) getAnalyzer(instance.getClass());
        return analyzer.getInstanceAnalyzer(instance);
    }

    private InstanceEffectsAnalyzer<T> getInstanceAnalyzer(T instance) {
        // bind the object in the GC monitor and use its key to track the analyzer
        int instanceKey = GCMonitor.get().bind(instance);

        if (!instanceAnalyzers.containsKey(instanceKey)) {
            instanceAnalyzers.put(instanceKey, new InstanceEffectsAnalyzer<>(this));
        }

        return instanceAnalyzers.get(instanceKey);
    }

    public static <T> EffectsAnalyzer<T> getAnalyzer(Class<T> clazz) {
        if (!effectsAnalyzers.containsKey(clazz)) {
            throw new IllegalStateException("Analyzer accessed before initialized!");
        }

        return (EffectsAnalyzer<T>) effectsAnalyzers.get(clazz);
    }

    public static <T> void setupAnalyzer(Class<T> clazz, String... possibleEffects) {
        if (effectsAnalyzers.containsKey(clazz)) {
            throw new IllegalStateException("Analyzer set up twice!");
        }

        effectsAnalyzers.put(clazz, new EffectsAnalyzer<>(Set.of(possibleEffects)));
    }

    public Set<String> getPossibleEffects() {
        return possibleEffects;
    }

    public Set<String> getWarnings() {
        return warnings;
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addTestedEffect(String sideEffect) {
        testedEffects.add(sideEffect);
    }

    public Set<String> getUntestedEffects() {
        return possibleEffects
                .stream()
                .filter(effect -> !testedEffects.contains(effect))
                .collect(Collectors.toSet());
    }
}
