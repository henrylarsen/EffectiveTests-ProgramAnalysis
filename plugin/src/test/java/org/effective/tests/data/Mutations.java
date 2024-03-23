package org.effective.tests.data;

public class Mutations {

    private class O {
        public int a;
        private int b, c;

        public O(int x, int y) {
            a = x;
            b = y;
            c = 0;
        }

    }
    private O obj;

    public Mutations() {
        obj = new O(3 ,4);
    }

    public O getO() {
        return obj;
    }

    public void setA(int x) {
        obj.a = x;
    }
}
