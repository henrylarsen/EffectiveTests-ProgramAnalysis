package org.effective.tests.data;

import org.effective.tests.EffectiveTest;
import org.effective.tests.data.inject.AMod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EffectiveTest(AMod.class)
public class AModTest {
    @Test
    void test1() {
        // creating classes
        AMod am1 = new AMod();
        AMod am2;
        am2 = new AMod();

        // using methods
        am1.setA(3);
        am1.setB(4);

        // accessing fields
        int aField = am1.getA();
        int cField;
        cField = am1.getC();
        int dField = am1.d;

        // assertions
        assertEquals(3, aField);
        assertEquals(3, cField); // does not have a code injection b/c C was never modified, so no need to register the assert
        assertEquals(3, dField);
        assertEquals(3, am1.sendThree());

        // using a method with multiple effects and using getters to obtain the values


        // branching if condition, will not record b/c no intersect values
        am1.foo(13);
        if (Math.random() * 100 < 50) {
            assertEquals(13, am1.getA());
            assertEquals(13, am1.getC());
        } else {
            assertEquals(13, am1.getA());
        }

        am1.d = 21;
        assertEquals(21, am1.d);
    }
}
