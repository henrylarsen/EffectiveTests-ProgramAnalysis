package org.effective.tests.data;

public class FieldMods {
    private int a;
    private int b;

    public FieldMods() {
        a = 0;
        b = 1;
    }

    public int getA() {
        return a;
    }

    public void setA(int x) {
        a = x;
    }

    // Should not be added as an effect because b has no getter
    public void setB(int x) { b = x; }
}
