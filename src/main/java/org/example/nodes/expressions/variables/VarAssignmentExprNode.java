package org.example.nodes.expressions.variables;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.nodes.expressions.EasyScriptExprNode;
import static com.oracle.truffle.api.frame.FrameSlotKind.*;

@NodeChild("initializerExpr")
@NodeField(name = "depth", type = int.class)
@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class VarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();
    protected abstract int getDepth();

    @ExplodeLoop
    protected final VirtualFrame getFrame(VirtualFrame frame) {
        for (int i = 0; i < this.getDepth(); i++)
            frame = (VirtualFrame) frame.getArguments()[0];

        return frame;
    }

    @Specialization(guards = "targetFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "targetFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(
            @SuppressWarnings("unused") VirtualFrame frame, int value,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        targetFrame.getFrameDescriptor().setSlotKind(this.getFrameSlot(), Int);
        targetFrame.setInt(this.getFrameSlot(), value);
        return value;
    }

    @Specialization(replaces = "intAssignment",
            guards = "targetFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "targetFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleAssignment(
            @SuppressWarnings("unused") VirtualFrame frame, double value,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        targetFrame.getFrameDescriptor().setSlotKind(this.getFrameSlot(), Double);
        targetFrame.setDouble(this.getFrameSlot(), value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment"},
            guards = "targetFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "targetFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean")
    protected boolean boolAssignment(
            @SuppressWarnings("unused") VirtualFrame frame, boolean value,
            @Cached("getFrame(frame)") VirtualFrame targetFrame
    ) {
        targetFrame.getFrameDescriptor().setSlotKind(this.getFrameSlot(), Boolean);
        targetFrame.setBoolean(this.getFrameSlot(), value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment", "boolAssignment"})
    protected Object objectAssignment(
            @SuppressWarnings("unused") VirtualFrame frame, Object value,
            @Cached("getFrame(frame)") VirtualFrame targetFrame) {
        targetFrame.getFrameDescriptor().setSlotKind(this.getFrameSlot(), Object);
        targetFrame.setObject(this.getFrameSlot(), value);
        return value;
    }
}