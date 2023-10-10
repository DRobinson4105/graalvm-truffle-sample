package org.example.nodes.expressions.literals;

import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.expressions.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public final class UndefinedLiteralExprNode extends EasyScriptExprNode {
    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(Undefined.instance);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(Undefined.instance);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return Undefined.instance;
    }
}