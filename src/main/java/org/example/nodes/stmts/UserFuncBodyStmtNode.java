package org.example.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.ReturnException;
import org.example.runtime.Undefined;

import java.util.List;

public final class UserFuncBodyStmtNode extends EasyScriptStmtNode {
    @Children
    private final EasyScriptStmtNode[] stmts;

    public UserFuncBodyStmtNode(List<EasyScriptStmtNode> stmts) {
        this.stmts = stmts.toArray(new EasyScriptStmtNode[]{});
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        try {
            for (EasyScriptStmtNode stmt : this.stmts) {
                    stmt.executeStatement(frame);
            }
        } catch (ReturnException e) {
            return e.returnValue;
        }

        return Undefined.INSTANCE;
    }
}
