package org.example.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.nodes.exprs.EasyScriptExprNode;

public final class IntLiteralExprNode extends EasyScriptExprNode {
    private final int value;

    public IntLiteralExprNode(int value) {
        this.value = value;
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return this.value != 0;
    }

    @Override
    public int executeInt(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
