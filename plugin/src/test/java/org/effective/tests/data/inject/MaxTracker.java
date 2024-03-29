package org.effective.tests.data.inject;

//import org.effective.tests.EffectsAnalyzer;

public class MaxTracker {

//    static {
//        EffectsAnalyzer.setupAnalyzer(MaxTracker.class, "highestEven:25", "getHighestOdd:29", "trackNumber:20", "highestEven:15", "trackNumber:16");
//    }

    private int highestEven = Integer.MIN_VALUE;

    private int highestOdd = Integer.MIN_VALUE;

    // Constructor
    public MaxTracker() {
    }

    // Adds number to track in this class. Returns whether it is higher than the highest even
    public boolean trackNumber(int number) {
        if (number % 2 == 0) {
            // Check if the number is even
            boolean higher = number > highestEven;
            highestEven = Math.max(number, highestEven);
//            EffectsAnalyzer.getInstanceAnalyzer(this).registerMutation("highestEven", 15);
//            EffectsAnalyzer.getInstanceAnalyzer(this).registerReturn("trackNumber",16);
            return higher;
        } else {
            boolean higher = number > highestOdd;
            highestOdd = Math.max(number, highestOdd);
//            EffectsAnalyzer.getInstanceAnalyzer(this).registerReturn("trackNumber",20);
            return higher;
        }
    }

    public int getHighestEven() {
//        EffectsAnalyzer.getInstanceAnalyzer(this).registerRead("highestEven");
        return highestEven;
    }

    public int getHighestOdd() {
//        EffectsAnalyzer.getInstanceAnalyzer(this).registerReturn("getHighestOdd",29);
        return highestOdd = 1;
    }
}
