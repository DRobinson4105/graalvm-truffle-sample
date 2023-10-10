package org.example.nodes.statements;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.expressions.EasyScriptExprNode;

public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode expr;
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
        return this.discardExpressionValue ? Undefined.instance : exprResult;
    }
}
