package org.effective.tests.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Field;

import java.util.*;
import java.util.stream.Collectors;

public class ProgramContext {
    private Map<BlockStmtWrapper, List<Effect>> effects;
    private Set<Field> fields;
    public ProgramContext() {
        effects = new HashMap();
        fields = new HashSet();
    }

    public Map<BlockStmtWrapper, List<Effect>> getEffectMap() {
        return effects;
    }

    public void addEffect(BlockStmt block, Effect e) {
        BlockStmtWrapper blockKey = new BlockStmtWrapper(block, block.getBegin().get().line);
        List ctxList = effects.get(blockKey);
        if (ctxList == null) {
            ctxList = new ArrayList<>();
        }
        ctxList.add(e);
        effects.put(blockKey, ctxList);
    }

    public Field getField(String name) {
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public Set<Field> getAvailableFields() {
        return fields.stream().filter(field -> field.isAvailable()).collect(Collectors.toSet());
    }

    public void addField(Field f) {
        fields.add(f);
    }

    // For testing purposes
    public boolean containsEffect(Effect e) {
        for (Map.Entry<BlockStmtWrapper, List<Effect>> effectList : effects.entrySet()) {
            if (effectList.getValue().contains(e)) {
                return true;
            }
        };
        return false;
    }

    public List<Effect> getAllEffects() {
        List<Effect> allEffects = new ArrayList();
        for (Map.Entry<BlockStmtWrapper, List<Effect>> entry : effects.entrySet()) {
            allEffects.addAll(entry.getValue());
        };
        return allEffects;
    }

}
