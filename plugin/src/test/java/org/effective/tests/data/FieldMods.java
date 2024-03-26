package org.effective.tests.data;

public class FieldMods {
    private int a, b, c;

    public FieldMods() {
        a = 0;
        b = 1;
        c = 2;
    }

    public int getA() {
        return a;
    }

    public int getC() {
        return c;
    }

    public void setA(int x) {
        a = x;
    }

    public void foo(int x) {
        if (true) {
            a = x;
            c = x;
        } else {
            b = x;
        }
    }

    // Should not be added as a testable effect because b has no getter
    public void setB(int x) {
        b = x;
    }
}
