package user.study;

import org.effective.tests.EffectiveTest;
import org.junit.Test;

// TODO Task 1: Run this test class from the command line with this command:
//      ./gradlew :testproject:test --tests "user.study.TestMaxTracker1"
//  Observe the reported missing effects. Using the errors as advice complete this test suite.

@EffectiveTest(MaxTracker1.class)
public class TestMaxTracker1 {

    @Test
    public void testMaxTracker1() {
        MaxTracker1 tracker = new MaxTracker1();
        tracker.trackNumber(2);
    }

}

