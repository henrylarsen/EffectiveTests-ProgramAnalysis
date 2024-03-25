package org.effective.tests.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Field;

import java.util.*;
import java.util.stream.Collectors;

public class EffectContext {
    private Map<BlockStmtWrapper, List<Effect>> effects;
    private final Set<Field> fields;

    /**
     * Context class for an EffectCollector.
     * @param fields list of all fields within that method;
     * necessary to determine whether an effect is testable.
     */
    public EffectContext(Set<Field> fields) {
        effects = new HashMap();
        this.fields = Collections.unmodifiableSet(fields);
    }

    public EffectContext() {
        effects = new HashMap();
        this.fields = Collections.unmodifiableSet(new HashSet<Field>());
    }

    // TODO: filter for testable effects once structure is finalized
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

    public List<Effect> getAllEffects() {
        List<Effect> allEffects = new ArrayList();
        for (Map.Entry<BlockStmtWrapper, List<Effect>> entry : effects.entrySet()) {
            allEffects.addAll(entry.getValue());
        };
        return allEffects;
    }

    public List<Effect> getAllTestableEffects() {
        return getAllEffects().stream().filter(e -> e.isTestable()).collect(Collectors.toList());
    }

}
