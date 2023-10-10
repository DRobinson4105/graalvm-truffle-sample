package org.example.nodes.statements.blocks;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.nodes.control.ReturnException;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.statements.EasyScriptStmtNode;

import java.util.List;

public final class FuncBodyStmtNode extends EasyScriptStmtNode {
    @Children
    private final EasyScriptStmtNode[] stmts;

    public FuncBodyStmtNode(List<EasyScriptStmtNode> stmts) {
        this.stmts = stmts.toArray(new EasyScriptStmtNode[]{});
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        try {
            for (EasyScriptStmtNode stmt : this.stmts) {
                    stmt.executeStatement(frame);
            }
        } catch (ReturnException e) {
            return e.getResult();
        }

        return Undefined.instance;
    }
}
