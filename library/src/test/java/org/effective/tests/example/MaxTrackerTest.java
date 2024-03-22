package org.effective.tests.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaxTrackerTest {
    @Test
    public void testMaxTracker0() {
        MaxTracker tracker = new MaxTracker();
        tracker.trackNumber(2);
    }

    @Test
    public void testMaxTracker50() {
        MaxTracker tracker = new MaxTracker();
        assertTrue(tracker.trackNumber(2));
    }

    @Test
    public void testMaxTracker100() {
        MaxTracker tracker = new MaxTracker();
        assertTrue(tracker.trackNumber(2));
        assertEquals(2, tracker.getHighestEven());
    }

}
