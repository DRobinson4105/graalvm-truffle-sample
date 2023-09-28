package org.example.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.nodes.control.DirectBreakException;

public class BreakStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new DirectBreakException(0);
    }
}
