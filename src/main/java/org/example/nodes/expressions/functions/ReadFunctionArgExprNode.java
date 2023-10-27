package org.example.nodes.expressions.functions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.expressions.EasyScriptExprNode;

public final class ReadFunctionArgExprNode extends EasyScriptExprNode {
    private final int index;
    private final int depth;

    @ExplodeLoop
    protected final VirtualFrame getFrame(VirtualFrame frame) {
        for (int i = 0; i < depth; i++)
            frame = (VirtualFrame) frame.getArguments()[0];

        return frame;
    }

    public ReadFunctionArgExprNode(int index) {
        this.index = index;
        this.depth = 0;
    }

    public ReadFunctionArgExprNode(int index, int depth) {
        this.index = index;
        this.depth = depth;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // traverse to frame containing the argument
        frame = getFrame(frame);

        Object[] arguments = frame.getArguments();
        return this.index < arguments.length ? arguments[this.index] : Undefined.instance;
    }
}