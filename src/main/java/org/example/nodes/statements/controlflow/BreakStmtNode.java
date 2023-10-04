package org.example.nodes.statements.controlflow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.nodes.control.DirectBreakException;
import org.example.nodes.statements.EasyScriptStmtNode;

public class BreakStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new DirectBreakException(0);
    }
}
