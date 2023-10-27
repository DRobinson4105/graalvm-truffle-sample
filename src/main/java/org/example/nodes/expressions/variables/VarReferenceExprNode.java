package org.example.nodes.expressions.variables;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeField(name = "frameSlot", type = int.class)
@NodeField(name = "depth", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class VarReferenceExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();
    protected abstract int getDepth();

    @ExplodeLoop
    protected final VirtualFrame getFrame(VirtualFrame frame) {
        for (int i = 0; i < this.getDepth(); i++)
            frame = (VirtualFrame) frame.getArguments()[0];

        return frame;
    }

    @Specialization(guards = "targetFrame.isInt(getFrameSlot())")
    protected int readInt(
            @SuppressWarnings("unused") VirtualFrame frame,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        return targetFrame.getInt(this.getFrameSlot());
    }

    @Specialization(replaces = "readInt", guards = "targetFrame.isDouble(getFrameSlot())")
    protected double readDouble(
            @SuppressWarnings("unused") VirtualFrame frame,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        return targetFrame.getDouble(this.getFrameSlot());
    }

    @Specialization(guards = "targetFrame.isBoolean(getFrameSlot())")
    protected boolean readBoolean(
            @SuppressWarnings("unused") VirtualFrame frame,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        return targetFrame.getBoolean(this.getFrameSlot());
    }

    @Specialization(replaces = {"readInt", "readDouble", "readBoolean"})
    protected Object readObject(
            @SuppressWarnings("unused") VirtualFrame frame,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        return targetFrame.getObject(this.getFrameSlot());
    }
}