package user.study;

public class MaxTracker1 {
    private int highestEven = Integer.MIN_VALUE;
    private int highestOdd = Integer.MIN_VALUE;

    // Constructor
    public MaxTracker1() {
    }

    // Adds number to track in this class.
    // Returns whether it is higher than the highest even/odd
    public boolean trackNumber(int number) {
        if (number % 2 == 0) { // Check if the number is even
            boolean higher = number > highestEven;
            highestEven = Math.max(number, highestEven);
            return higher;
        } else {
            boolean higher = number > highestOdd;
            highestOdd = Math.max(number, highestOdd);
            return higher;
        }
    }

    public int getHighestEven() {
        return highestEven;
    }

    public int getHighestOdd() {
        return highestOdd;
    }
}
