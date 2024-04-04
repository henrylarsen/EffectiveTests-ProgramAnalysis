package user.study;

import org.effective.tests.EffectiveTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO Task 2: Run each of the following test cases individually from the Intellij test runner. For each of the tests
//  use the output as a guide to fix the underlying issue while making minimal changes (adding 1 or 2 lines of code)

// TODO Task 2.3: Value agnostic static analysis is tricky, which is why 2.2 had an error even if we as humans could tell
//  its effects were tested. Because of the if statements, we can't be certain what operation the assert is checking.
//  Which of the following ways of handling this issue seems like a better fit for this tool:
//  - Assume asserts like that are asserting neither the even nor odd tracker (current implementation). This can lead to
//    false negatives like you see here.
//  - Assume asserts like that are asserting BOTH the even and odd tracker. This could lead to false negatives.

@EffectiveTest(MaxTracker2.class)
public class TestMaxTracker2 {

    @Test // 2.1:
    public void testMaxTracker1() {
        MaxTracker2 tracker = new MaxTracker2();
        int num;
        for (int i = 0; i < 4; i++) {
            boolean result = tracker.trackNumber(i);
            num = tracker.getHighestEven();
            num = tracker.getHighestOdd();
            assertTrue(i >= num);
            assertTrue(result);
        }
    }

    @Test // 2.2:
    public void testMaxTracker2() {
        MaxTracker2 tracker = new MaxTracker2();
        int num;
        for (int i = 0; i < 4; i++) {
            boolean result = tracker.trackNumber(i);
            assertTrue(result);
            if (i % 2 == 0) {
                num = tracker.getHighestEven();
            } else {
                num = tracker.getHighestOdd();
            }
            assertEquals(num, i);
        }
    }

}
