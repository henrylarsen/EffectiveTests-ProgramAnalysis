package org.effective.tests.data;

public class Duplication {
    public int x, y;
    public void bar() {
        int x, y;
        x = 10;
        return;
    }

    public void foo() {
        x = 10;
        return;
    }
}

