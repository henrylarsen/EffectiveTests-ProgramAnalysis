package org.effective.tests.example;

import org.effective.tests.EffectsAnalyzer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaxTrackerTestInjected {
    @Test
    public void testMaxTracker0() {
        MaxTrackerInjected tracker = new MaxTrackerInjected();
        tracker.trackNumber(2);
    }

    @Test
    public void testMaxTracker50() {
        MaxTrackerInjected tracker = new MaxTrackerInjected();
        assertTrue(tracker.trackNumber(2)); /* INJECT START */ EffectsAnalyzer.getInstance(tracker).registerAssert("trackNumber"); /* INJECT END */
    }

    @Test
    public void testMaxTracker100() {
        MaxTrackerInjected tracker = new MaxTrackerInjected();
        assertTrue(tracker.trackNumber(2)); /* INJECT START */ EffectsAnalyzer.getInstance(tracker).registerAssert("trackNumber"); /* INJECT END */
        assertEquals(2, tracker.getHighestEven()); /* INJECT START */ EffectsAnalyzer.getInstance(tracker).registerAssert("highestEven"); /* INJECT END */
    }

    // not actually injected, just to show it works:
    @AfterAll
    public static void afterAll() {
        EffectsAnalyzer<MaxTrackerInjected> analyzer = EffectsAnalyzer.getAnalyzer(MaxTrackerInjected.class);
        Set<String> untestedEffects = analyzer.getUntestedEffects();

        assertEquals(2, untestedEffects.size());
        assertTrue(untestedEffects.contains("trackNumber:26"));
        assertTrue(untestedEffects.contains("highestOdd:27"));
    }
}
