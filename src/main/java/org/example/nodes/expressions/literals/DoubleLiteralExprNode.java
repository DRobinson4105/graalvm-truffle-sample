package org.example.nodes.expressions.literals;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.example.nodes.expressions.EasyScriptExprNode;

public final class DoubleLiteralExprNode extends EasyScriptExprNode {
    private final double value;

    public DoubleLiteralExprNode(double value) {
        this.value = value;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(this.value);
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return this.value != 0.0;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
