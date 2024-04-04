package org.effective.tests.data;

import org.effective.tests.EffectiveTest;
import org.effective.tests.data.inject.FModders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EffectiveTest(FModders.class)
public class FModdersTest2 {
    @Test
    void test1() {
        FModders fm1 = new FModders();
        assertEquals(4, fm1.sendFour());
    }
}
