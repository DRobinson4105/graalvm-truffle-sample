package org.example.nodes.statements.blocks;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.statements.EasyScriptStmtNode;

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
        for (EasyScriptStmtNode stmt : this.stmts)
            stmt.executeStatement(frame);

        return Undefined.instance;
    }
}
