package org.effective.tests.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.Objects;

public class BlockStmtWrapper {
    private BlockStmt blockStmt;
    private int lineNumber;

    public BlockStmtWrapper(BlockStmt blockStmt, int lineNumber) {
        this.blockStmt = blockStmt;
        this.lineNumber = lineNumber;
    }

    public BlockStmt getBlockStmt() {
        return blockStmt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BlockStmtWrapper other = (BlockStmtWrapper) obj;
        return Objects.equals(blockStmt, other.blockStmt) && lineNumber == other.lineNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockStmt, lineNumber);
    }
}
