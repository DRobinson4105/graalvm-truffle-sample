package org.example.nodes.exprs;

import org.example.runtime.Undefined;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public final class UndefinedLiteralExprNode extends EasyScriptExprNode {
    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(Undefined.INSTANCE);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(Undefined.INSTANCE);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return Undefined.INSTANCE;
    }
}