package org.example.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.runtime.Undefined;

import java.util.List;

public final class BlockStmtNode extends EasyScriptStmtNode {
    @Children
    public final EasyScriptStmtNode[] stmts;

    public BlockStmtNode(List<EasyScriptStmtNode> stmts) {
        this.stmts = stmts.toArray(new EasyScriptStmtNode[]{});
    }

    @Override
    @ExplodeLoop
    public Object executeStatement(VirtualFrame frame) {
        if (this.stmts.length == 0)
            return Undefined.INSTANCE;

        int lastStatementIndex = this.stmts.length - 1;
        for (int i = 0; i < lastStatementIndex; i++)
            stmts[i].executeStatement(frame);

        return stmts[lastStatementIndex].executeStatement(frame);
    }
}
