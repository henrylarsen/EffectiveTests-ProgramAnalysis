package org.effective.tests.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FModdersTest {
    @Test
    void test1() {
        // creating a class
        FModders fm1 = new FModders();
        FModders fm2;
        fm2 = new FModders();

        // using methods
        fm1.setA(3);

        // accessing class fields
        int varDeclareField = fm1.d;
        int varDeclareFieldGetter = fm1.getA(); // getters will just substring to get the class field
        int assignField;
        assignField = fm1.d;
        int assignFieldGetter;
        assignFieldGetter = fm1.getA();

        assertEquals(3, varDeclareField);
        assertEquals(3, varDeclareFieldGetter);
        assertEquals(3, fm1.getA());

        fm1.foo(13);
        assertEquals(13, fm1.getA());
//        assertEquals(13, fm1.getC());

        FModders fm3;
        if (Math.random() * 100 < 50) {
            fm3 = new FModders();
            fm3.setA(1);
        } else {
            fm3 = new FModders();
        }
    }
}
