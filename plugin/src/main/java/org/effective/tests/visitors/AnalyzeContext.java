package org.effective.tests.visitors;

import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.Field;
import org.effective.tests.effects.MethodData;

import java.util.*;

public class AnalyzeContext {
    EffectContext classContext;
    Map<MethodData, Set<Field>> usedMethodsAndCoverage;
    Map<String, Map<Field, MethodData>> classInstances; // tracks what fields have been modified so far, fields always in lower case
    Map<String, Pair<String, String>> variableInstances; // variable name to classInstance.field, will not work with class.method() return value. pair is classInstance and field

    public AnalyzeContext(EffectContext effectContext) {
        classContext = effectContext;
        classInstances = new HashMap<>();
        variableInstances = new HashMap<>();
        usedMethodsAndCoverage = new HashMap<>();
    }

    // duplicate the AnalyzeContext without the classInstances
    public AnalyzeContext blankCopy() {
        return new AnalyzeContext(classContext);
    }

    public void intersectInstances(AnalyzeContext ac) {
        Map<MethodData, Set<Field>> intersectMAC = new HashMap<>();
        Map<String, Map<Field, MethodData>> intersectInstances = new HashMap<>();
        Map<String, Pair<String, String>> intersectVariables = new HashMap<>();

        for (Map.Entry<MethodData, Set<Field>> entry : this.usedMethodsAndCoverage.entrySet()) {
            MethodData key = entry.getKey();
            if (ac.usedMethodsAndCoverage.containsKey(key)) {
                Set<Field> intersectFields = new HashSet<>(entry.getValue());
                intersectFields.retainAll(ac.usedMethodsAndCoverage.get(key));
                intersectMAC.put(key, intersectFields);
            }
        }

        for (Map.Entry<String, Map<Field, MethodData>> entry : this.classInstances.entrySet()) {
            String instance = entry.getKey();
            if (ac.classInstances.containsKey(instance)) {
                Map<Field, MethodData> intersectMap = intersectInnerMap(entry.getValue(), ac.classInstances.get(instance));
                intersectInstances.put(instance, intersectMap);
            }
        }

        for (Map.Entry<String, Pair<String, String>> entry : this.variableInstances.entrySet()) {
            String key = entry.getKey();
            if (ac.variableInstances.containsKey(key)) {
                Pair<String, String> value = entry.getValue();
                if (value.equals(ac.variableInstances.get(key))) {
                    intersectVariables.put(key, value);
                }
            }
        }

        this.usedMethodsAndCoverage = intersectMAC;
        this.classInstances = intersectInstances;
        this.variableInstances = intersectVariables;
    }

    private Map<Field, MethodData> intersectInnerMap(Map<Field, MethodData> m1, Map<Field, MethodData> m2) {
        Map<Field, MethodData> intersectMap = new HashMap<>();
        for (Map.Entry<Field, MethodData> entry : m1.entrySet()) {
            Field key = entry.getKey();
            if (m2.containsKey(key)) {
                MethodData value = entry.getValue();
                if (value.equals(m2.get(key))) {
                    intersectMap.put(key, value);
                }
            }
        }
        return intersectMap;
    }

    // any overrides will be taken from ac
    public void unionInstances(AnalyzeContext ac) {
        for (Map.Entry<MethodData, Set<Field>> entry : ac.usedMethodsAndCoverage.entrySet()) {
            MethodData key = entry.getKey();
            if (this.usedMethodsAndCoverage.containsKey(key)) {
                this.usedMethodsAndCoverage.get(key).addAll(entry.getValue());
            } else {
                this.usedMethodsAndCoverage.put(key, entry.getValue());
            }
        }
        for (Map.Entry<String, Map<Field, MethodData>> entry : ac.classInstances.entrySet()) {
            String key = entry.getKey();
            if (this.classInstances.containsKey(key)) {
                Map<Field, MethodData> map1 = entry.getValue();
                Map<Field, MethodData> map2 = this.classInstances.get(key);
                map2.putAll(map1);
            } else {
                this.classInstances.put(key, entry.getValue());
            }
        }
        this.variableInstances.putAll(ac.variableInstances);
    }
}
