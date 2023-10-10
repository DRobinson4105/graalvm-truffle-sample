package org.example.nodes.statements;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.expressions.EasyScriptExprNode;

public final class PrintStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode messageExpr;

    public PrintStmtNode(EasyScriptExprNode messageExpr) {
        this.messageExpr = messageExpr;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        System.out.println(messageExpr.executeGeneric(frame));
        return Undefined.instance;
    }
}
