package org.example.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.nodes.exprs.EasyScriptExprNode;
import org.example.nodes.stmts.EasyScriptStmtNode;
import org.example.runtime.Undefined;

public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode expr;
    private final boolean discardExpressionValue;

    public ExprStmtNode(EasyScriptExprNode expr) {
        this(expr, false);
    }

    public ExprStmtNode(EasyScriptExprNode expr, boolean discardExpressionValue) {
        this.expr = expr;
        this.discardExpressionValue = discardExpressionValue;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object exprResult = this.expr.executeGeneric(frame);
        return this.discardExpressionValue ? Undefined.INSTANCE : exprResult;
    }
}
