package org.example.nodes.expressions.variables;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("initializerExpr")
@NodeField(name = "frameSlot", type = int.class)
@NodeField(name = "depth", type = int.class)
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

    @Specialization(guards = "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(VirtualFrame frame, int value) {
        int frameSlot = this.getFrameSlot();
        frame = getFrame(frame);
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Int);
        frame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(replaces = "intAssignment",
            guards = "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleAssignment(VirtualFrame frame, double value) {
        var frameSlot = this.getFrameSlot();
        frame = getFrame(frame);
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
        frame.setDouble(frameSlot, value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment"},
            guards = "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean")
    protected boolean boolAssignment(VirtualFrame frame, boolean value) {
        var frameSlot = this.getFrameSlot();
        frame = getFrame(frame);
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Boolean);
        frame.setBoolean(frameSlot, value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment", "boolAssignment"})
    protected Object objectAssignment(VirtualFrame frame, Object value) {
        var frameSlot = this.getFrameSlot();
        frame = getFrame(frame);
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
        frame.setObject(frameSlot, value);
        return value;
    }
}