package org.example.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.staticobject.StaticShape;
import org.example.runtime.Undefined;

import java.util.Arrays;

public class ReadFunctionArgExprNode extends EasyScriptExprNode {
    private final int index;

    public ReadFunctionArgExprNode(int index) {
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] arguments = frame.getArguments();
        return this.index < arguments.length ? arguments[this.index] : Undefined.INSTANCE;
    }
}