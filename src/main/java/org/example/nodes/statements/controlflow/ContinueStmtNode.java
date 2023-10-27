package org.example.nodes.statements.controlflow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.nodes.control.ContinueException;
import org.example.nodes.statements.EasyScriptStmtNode;

public final class ContinueStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ContinueException();
    }
}
