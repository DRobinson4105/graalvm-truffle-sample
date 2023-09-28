package org.example.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.ReturnException;
import org.example.nodes.exprs.EasyScriptExprNode;
import org.example.runtime.Undefined;

public class ReturnStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode expr;

    public ReturnStmtNode(EasyScriptExprNode expr) {
        this.expr = expr;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ReturnException(this.expr.executeGeneric(frame));
    }
}
