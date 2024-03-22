package org.effective.tests.example;

import org.effective.tests.EffectsAnalyzer;

public class MaxTrackerInjected {
    private int highestEven = Integer.MIN_VALUE;
    private int highestOdd = Integer.MIN_VALUE;

    // INJECT START
    static {
        EffectsAnalyzer.setupAnalyzer(MaxTrackerInjected.class, "highestEven:23", "highestOdd:27", "trackNumber:22", "trackNumber:26");
    }
    // INJECT END

    // Constructor
    public MaxTrackerInjected() {
    }

    // Adds number to track in this class. Returns whether it is higher than the highest even
    public boolean trackNumber(int number) {
        if (number % 2 == 0) { // Check if the number is even
            boolean higher = number > highestEven;
            highestEven = Math.max(number, highestEven); /* INJECT START */ EffectsAnalyzer.getInstance(this).registerMutation("highestEven", 23); /* INJECT END */
            /* INJECT START */ EffectsAnalyzer.getInstance(this).registerReturn("trackNumber", 22); /* INJECT END */ return higher;
        } else {
            boolean higher = number > highestOdd;
            highestOdd = Math.max(number, highestOdd); /* INJECT START */ EffectsAnalyzer.getInstance(this).registerMutation("highestOdd", 27); /* INJECT END */
            /* INJECT START */ EffectsAnalyzer.getInstance(this).registerReturn("trackNumber", 26); /* INJECT END */ return higher;
        }
    }

    public int getHighestEven() {
        /* INJECT START */ EffectsAnalyzer.getInstance(this).registerRead("highestEven"); /* INJECT END */ return highestEven;
    }

    public int getHighestOdd() {
        /* INJECT START */ EffectsAnalyzer.getInstance(this).registerRead("highestOdd"); /* INJECT END */ return highestOdd;
    }
}
