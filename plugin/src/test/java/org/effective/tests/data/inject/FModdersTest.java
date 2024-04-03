package org.effective.tests.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.effective.tests.EffectsAnalyzer;

public class FModdersTest {

    @Test
    void test1() {
        // creating classes
        FModders fm1 = new FModders();
        FModders fm2;
        fm2 = new FModders();
        // using methods
        fm1.setA(3);
        fm1.setB(4);
        // accessing fields
        int aField = fm1.getA();
        int cField;
        cField = fm1.getC();
        int dField = fm1.d;
        // assertions
        assertEquals(3, aField);
//        EffectsAnalyzer.getInstanceAnalyzer(fm1).registerAssert("a");
        // does not have a code injection b/c C was never modified, so no need to register the assert
        assertEquals(3, cField);
        assertEquals(3, dField);
        assertEquals(3, fm1.sendThree());
//        EffectsAnalyzer.getInstanceAnalyzer(fm1).registerAssert("sendThree");
        // using a method with multiple effects and using getters to obtain the values
        // branching if condition, will not record b/c no intersect values
        fm1.foo(13);
        if (Math.random() * 100 < 50) {
            assertEquals(13, fm1.getA());
//            EffectsAnalyzer.getInstanceAnalyzer(fm1).registerAssert("a");
            assertEquals(13, fm1.getC());
//            EffectsAnalyzer.getInstanceAnalyzer(fm1).registerAssert("c");
        } else {
            assertEquals(13, fm1.getA());
//            EffectsAnalyzer.getInstanceAnalyzer(fm1).registerAssert("a");
        }
        fm1.d = 21;
        assertEquals(21, fm1.d);
//        EffectsAnalyzer.getInstanceAnalyzer(fm1).registerAssert("d");
    }
}
