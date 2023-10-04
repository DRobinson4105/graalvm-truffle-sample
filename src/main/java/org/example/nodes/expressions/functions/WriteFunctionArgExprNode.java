package org.example.nodes.expressions.functions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.nodes.expressions.EasyScriptExprNode;

public final class WriteFunctionArgExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;
    private final int index;
    private final int depth;

    @ExplodeLoop
    VirtualFrame getFrame(VirtualFrame frame) {
        for (int i = 0; i < depth; i++)
            frame = (VirtualFrame) frame.getArguments()[0];

        return frame;
    }

    public WriteFunctionArgExprNode(EasyScriptExprNode initializerExpr, int index, int depth) {
        this.initializerExpr = initializerExpr;
        this.index = index;
        this.depth = depth;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        frame = getFrame(frame);
        Object value = this.initializerExpr.executeGeneric(frame);
        frame.getArguments()[index] = value;
        return value;
    }
}
