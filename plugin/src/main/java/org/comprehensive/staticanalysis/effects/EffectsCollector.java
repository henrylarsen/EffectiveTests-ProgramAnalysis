package org.comprehensive.staticanalysis.effects;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitorWithDefaults;
import java.util.List;
import java.util.Map;

public class EffectsCollector extends GenericVisitorWithDefaults<Map<BlockStmt, List<Effect>>, Void> {

}
