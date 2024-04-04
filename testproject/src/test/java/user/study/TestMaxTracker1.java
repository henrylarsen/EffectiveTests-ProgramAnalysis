package user.study;

import org.effective.tests.EffectiveTest;
import org.junit.Test;

// TODO Task 1: Run Effective on this test class and observe the 2 errors. Using the errors as advice, fix the
//  underlying issue.

@EffectiveTest(MaxTracker1.class)
public class TestMaxTracker1 {

    @Test
    public void testMaxTracker1() {
        MaxTracker1 tracker = new MaxTracker1();
        tracker.trackNumber(2);
    }

}

