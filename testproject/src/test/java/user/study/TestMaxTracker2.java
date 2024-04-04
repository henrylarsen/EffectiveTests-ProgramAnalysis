package user.study;

import org.effective.tests.EffectiveTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO Task 2 (Predictive): Observe the two following tests. For each of the tests, predict the output of Effective
//  assuming that that test is the only one testing MaxTracker

@EffectiveTest(MaxTracker2.class)
public class TestMaxTracker2 {

    @Test // 2.1:
    public void testMaxTracker1() {
        MaxTracker2 tracker = new MaxTracker2();
        int num;
        for (int i = 0; i < 4; i++) {
            tracker.trackNumber(i);
            num = tracker.getHighestEven();
            num = tracker.getHighestOdd();
            assertTrue(i >= num);
        }
    }

    @Test // 2.2:
    public void testMaxTracker2() {
        MaxTracker2 tracker = new MaxTracker2();
        int num;
        for (int i = 0; i < 4; i++) {
            tracker.trackNumber(i);
            if (i % 2 == 0) {
                num = tracker.getHighestEven();
            } else {
                num = tracker.getHighestOdd();
            }
            assertEquals(num, i);
        }
    }

}
