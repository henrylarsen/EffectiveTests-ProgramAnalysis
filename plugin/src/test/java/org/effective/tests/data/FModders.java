package org.effective.tests.data;

public class FModders {
    private int a, b, c;
    public int d, e;

    public FModders() {
        a = 0;
        b = 1;
        c = 2;
    }

    public void setA(int x) {
        a = x;
    }

    public int getA() {
        return a;
    }

    public int getC() {
        return c;
    }

    public void foo(int x) {
        if (true) {
            a = x;
            c = x;
        } else {
            b = x;
        }
    }

    public int sendThree() {
        return 3;
    }

    // Should not be added as a testable effect because b has no getter
    public void setB(int x) {
        b = x;
    }
}
