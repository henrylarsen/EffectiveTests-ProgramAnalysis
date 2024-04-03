package org.effective.tests.visitors;

import org.effective.tests.effects.Field;
import org.effective.tests.effects.MethodData;
import org.effective.tests.staticVariables.VarType;

import java.util.*;

public class AnalyzeContext {
    EffectContext classContext;
    Map<MethodData, Set<VarType>> usedMethodsAndCoverage; // the returned data from analysis
    Map<String, Map<Field, MethodData>> classInstances; // tracks what fields have been modified by what methods so far for class instances
    Map<String, VarType> variableInstances; // Variable name to classInstance.field or return of method, getters return fields

    public AnalyzeContext(EffectContext effectContext) {
        classContext = effectContext;
        classInstances = new HashMap<>();
        variableInstances = new HashMap<>();
        usedMethodsAndCoverage = new HashMap<>();
    }

    // copy AnalyzeContext for use in control flow
    public AnalyzeContext copy() {
        AnalyzeContext newContext = new AnalyzeContext(classContext);
        newContext.variableInstances = new HashMap<>(this.variableInstances);
        newContext.classInstances = new HashMap<>(this.classInstances);
        for (Map.Entry<MethodData, Set<VarType>> entry : this.usedMethodsAndCoverage.entrySet()) {
            Set<VarType> newSet = new HashSet<>();
            newSet.addAll(entry.getValue());
            newContext.usedMethodsAndCoverage.put(entry.getKey(), newSet);
        }
        return newContext;
    }

    public void intersect(AnalyzeContext ac) {
        Map<MethodData, Set<VarType>> intersectMAC = new HashMap<>();
        Map<String, Map<Field, MethodData>> intersectInstances = new HashMap<>();
        Map<String, VarType> intersectVariables = new HashMap<>();

        for (Map.Entry<MethodData, Set<VarType>> entry : this.usedMethodsAndCoverage.entrySet()) {
            MethodData key = entry.getKey();
            if (ac.usedMethodsAndCoverage.containsKey(key)) {
                Set<VarType> intersectFields = new HashSet<>(entry.getValue());
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

        for (Map.Entry<String, VarType> entry : this.variableInstances.entrySet()) {
            String key = entry.getKey();
            if (ac.variableInstances.containsKey(key)) {
                VarType value = entry.getValue();
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
    public void union(AnalyzeContext ac) {
        for (Map.Entry<MethodData, Set<VarType>> entry : ac.usedMethodsAndCoverage.entrySet()) {
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
