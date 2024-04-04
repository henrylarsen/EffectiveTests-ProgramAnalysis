package user.study;

import org.effective.tests.EffectiveTest;
import org.junit.Test;

// TODO Task 1: Run Effective on this test class and observe the 2 errors. Using the errors as advice, fix the
//  underlying issue.

@EffectiveTest(MaxTracker.class)
public class TestMaxTracker1 {

    @Test
    public void testMaxTracker1() {
        MaxTracker tracker = new MaxTracker();
        tracker.trackNumber(2);
    }

}

