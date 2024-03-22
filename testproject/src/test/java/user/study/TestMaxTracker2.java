package user.study;

import org.junit.Test;

// TODO Task 2 (Predictive): Observe the two following tests. For each of the tests, predict the output of Effective
//  assuming that that test is the only one testing MaxTracker

// @EffectiveTest(user.study.MaxTracker.class)
public class TestMaxTracker2 {

    @Test // 2.1:
    public void testMaxTracker1() {
        MaxTracker tracker = new MaxTracker();
        int num;
        for (int i = 0; i < 4; i++) {
            tracker.trackNumber(i);
            num = tracker.getHighestEven();
            num = tracker.getHighestOdd();
            assert (i >= num);
        }
    }

    @Test // 2.2:
    public void testMaxTracker2() {
        MaxTracker tracker = new MaxTracker();
        int num;
        for (int i = 0; i < 4; i++) {
            tracker.trackNumber(i);
            if (i % 2 == 0) {
                num = tracker.getHighestEven();
            } else {
                num = tracker.getHighestOdd();
            }
            assert(num == i);
        }
    }

}
