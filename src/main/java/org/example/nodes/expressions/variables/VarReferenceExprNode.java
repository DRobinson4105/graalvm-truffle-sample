package org.example.nodes.expressions.variables;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
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

    @Specialization(guards = "getFrame(frame).isInt(getFrameSlot())")
    protected int readInt(VirtualFrame frame) {
        return this.getFrame(frame).getInt(this.getFrameSlot());
    }

    @Specialization(replaces = "readInt", guards = "getFrame(frame).isDouble(getFrameSlot())")
    protected double readDouble(VirtualFrame frame) {
        return this.getFrame(frame).getDouble(this.getFrameSlot());
    }

    @Specialization(guards = "getFrame(frame).isBoolean(getFrameSlot())")
    protected boolean readBoolean(VirtualFrame frame) {
        return this.getFrame(frame).getBoolean(this.getFrameSlot());
    }

    @Specialization(replaces = {"readInt", "readDouble", "readBoolean"})
    protected Object readObject(VirtualFrame frame) {
        return this.getFrame(frame).getObject(this.getFrameSlot());
    }
}