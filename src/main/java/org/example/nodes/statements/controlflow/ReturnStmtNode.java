package org.example.nodes.statements.controlflow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.nodes.control.ReturnException;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.statements.EasyScriptStmtNode;

public final class ReturnStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode expr;

    public ReturnStmtNode(EasyScriptExprNode expr) {
        this.expr = expr;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ReturnException(this.expr.executeGeneric(frame));
    }
}
