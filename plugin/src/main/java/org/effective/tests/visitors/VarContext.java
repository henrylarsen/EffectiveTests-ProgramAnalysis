package org.effective.tests.visitors;

import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.Field;

import java.util.*;

public class VarContext {
    private Set<Field> fields;
    private Map<Pair<String, Integer>, List<String>> localVars;

    public VarContext() {
        this.fields = new HashSet();
        this.localVars = new HashMap();
    }

    public Set<Field> getFields() {
        return fields;
    }

    public Field getField(String name) {
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public Map<Pair<String, Integer>, List<String>> getLocalVars() {
        return localVars;
    }

    public void addField(Field f) {
        fields.add(f);
    }

    public void addLocalVariables(String methodName, int methodLine, List<String> variableNames) {
        Pair<String, Integer> methodKey = new Pair(methodName, methodLine);
        List variableList = localVars.get(methodKey);
        if (variableList == null) {
            variableList = new ArrayList<>();
        }
        variableList.addAll(variableNames);
        localVars.put(methodKey, variableList);
    }

    public List<String> getLocalVariables(String methodName, int methodLine) {
        return localVars.get(new Pair(methodName, methodLine));
    }

    public boolean isLocalVariable(String methodName, int methodLine, String fieldName) {
        List<String> locals = getLocalVariables(methodName, methodLine);
        return locals != null && locals.contains(fieldName);
    }


}
