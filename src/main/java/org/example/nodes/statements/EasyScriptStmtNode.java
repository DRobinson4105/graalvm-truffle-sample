package org.example.nodes.statements;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.nodes.EasyScriptNode;

public abstract class EasyScriptStmtNode extends EasyScriptNode {
    public abstract Object executeStatement(VirtualFrame frame);
}
