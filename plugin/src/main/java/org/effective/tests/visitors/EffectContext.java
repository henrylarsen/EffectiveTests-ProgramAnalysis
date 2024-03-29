package org.effective.tests.visitors;

import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Field;
import org.effective.tests.effects.MethodData;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class EffectContext {
    private Map<MethodData, List<Effect>> effects;
    private VarContext vCtx;

    /**
     * Context class for an EffectCollector.
     * @param vars VarContext instance with a list of all class fields and local variables for that method;
     * necessary to determine whether an effect is testable.
     */
    public EffectContext(VarContext vars) {
        this.effects = new HashMap();
        this.vCtx = vars;
    }

    public Map<MethodData, List<Effect>> getEffectMap() {
        HashMap<MethodData, List<Effect>> filteredMap = new HashMap<>();

        for (Map.Entry<MethodData, List<Effect>> entry : effects.entrySet()) {
            MethodData key = entry.getKey();
            List<Effect> filteredEffects = isTestable(entry.getValue());

            if (!filteredEffects.isEmpty()) {
                filteredMap.put(key, filteredEffects);
            }
        }
        return filteredMap;
    }

    public void addEffect(String methodName, List<String> paramTypes, int methodLine, Effect e) {
        MethodData methodKey = new MethodData(methodName, paramTypes, methodLine);

        List ctxList = effects.get(methodKey);
        if (ctxList == null) {
            ctxList = new ArrayList<>();
        }
        ctxList.add(e);
        effects.put(methodKey, ctxList);
    }

    public Field getField(String name) {
        return vCtx.getField(name);
    }

    public Set<Field> getFields() {
        return vCtx.getFields();
    }

    public List<Effect> getAllEffects() {
        List<Effect> allEffects = new ArrayList();
        for (Map.Entry<MethodData, List<Effect>> entry : effects.entrySet()) {
            allEffects.addAll(entry.getValue());
        };
        return allEffects;
    }

    public List<Effect> getAllTestableEffects() {
        return isTestable(getAllEffects());
    }

    private List<Effect> isTestable(List<Effect> effects) {
        return effects.stream().filter(e -> e.isTestable()).collect(Collectors.toList());
    }

    public VarContext getVarCtx() {
        return vCtx;
    }

    public List<Effect> getMethodEffects(String methodName, List<String> paramTypes) {
        for (Map.Entry<MethodData, List<Effect>> entry : effects.entrySet()) {
            MethodData key = entry.getKey();
            if (key.methodName.equals(methodName) && key.paramTypes.equals(paramTypes)) {
                return entry.getValue();
            }
        }
        return new ArrayList<>();
    }
}
