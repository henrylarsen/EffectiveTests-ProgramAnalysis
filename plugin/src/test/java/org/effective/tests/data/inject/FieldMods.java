package org.effective.tests.data.inject;

//import org.effective.tests.EffectsAnalyzer;

public class FieldMods {

//    static {
//        EffectsAnalyzer.setupAnalyzer(FieldMods.class, "a:21", "c:17", "a:26", "c:27", "a:13");
//    }

    private int a, b, c;

    public FieldMods() {
        a = 0;
        b = 1;
        c = 2;
    }

    public int getA() {
//        EffectsAnalyzer.getInstanceAnalyzer(this).registerRead("a");
        return a;
    }

    public int getC() {
//        EffectsAnalyzer.getInstanceAnalyzer(this).registerRead("c");
        return c;
    }

    public void setA(int x) {
        a = x;
//        EffectsAnalyzer.getInstanceAnalyzer(this).registerMutation("a", 21);
    }

    public void foo(int x) {
        if (true) {
            a = x;
//            EffectsAnalyzer.getInstanceAnalyzer(this).registerMutation("c", 27);
            c = x;
//            EffectsAnalyzer.getInstanceAnalyzer(this).registerMutation("a", 26);
        } else {
            b = x;
        }
    }

    // Should not be added as a testable effect because b has no getter
    public void setB(int x) {
        b = x;
    }
}
