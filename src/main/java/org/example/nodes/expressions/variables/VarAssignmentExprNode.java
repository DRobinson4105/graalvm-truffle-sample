package org.example.nodes.expressions.variables;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.nodes.expressions.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.OnBefore;

import java.util.function.Consumer;

@NodeChild("initializerExpr")
@NodeField(name = "frameSlot", type = int.class)
@NodeField(name = "depth", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class VarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();
    protected abstract int getDepth();
    @CompilerDirectives.CompilationFinal
    private MaterializedFrame frame = null;

    protected final void assign(VirtualFrame frame, Consumer<VirtualFrame> valueSetter, FrameSlotKind frameSlotKind) {
        frame = getFrame(frame);
        frame.getFrameDescriptor().setSlotKind(this.getFrameSlot(), frameSlotKind);
        valueSetter.accept(frame);
    }

    @ExplodeLoop
    protected final VirtualFrame getFrame(VirtualFrame frame) {
        for (int i = 0; i < this.getDepth(); i++)
            frame = (VirtualFrame) frame.getArguments()[0];

        return frame;
    }

    @Specialization(guards = "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(VirtualFrame frame, int value) {
        assign(frame, curFrame -> curFrame.setInt(this.getFrameSlot(), value), FrameSlotKind.Int);
        return value;
    }

    @Specialization(replaces = "intAssignment",
            guards = "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleAssignment(VirtualFrame frame, double value) {
        assign(frame, curFrame -> curFrame.setDouble(this.getFrameSlot(), value), FrameSlotKind.Double);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment"},
            guards = "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "getFrame(frame).getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean")
    protected boolean boolAssignment(VirtualFrame frame, boolean value) {
        assign(frame, curFrame -> curFrame.setBoolean(this.getFrameSlot(), value), FrameSlotKind.Boolean);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment", "boolAssignment"})
    protected Object objectAssignment(VirtualFrame frame, Object value) {
        assign(frame, curFrame -> curFrame.setObject(this.getFrameSlot(), value), FrameSlotKind.Object);
        return value;
    }
}