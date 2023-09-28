package org.example.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class WriteFunctionArgExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;
    private final int index;

    public WriteFunctionArgExprNode(EasyScriptExprNode initializerExpr, int index) {
        this.initializerExpr = initializerExpr;
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object value = this.initializerExpr.executeGeneric(frame);
        frame.getArguments()[index] = value;
        return value;
    }
}
