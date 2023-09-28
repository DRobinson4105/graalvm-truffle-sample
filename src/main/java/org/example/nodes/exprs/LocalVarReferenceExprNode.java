package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "frameSlot", type = int.class)
public abstract class LocalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();

    @Specialization(guards = "frame.isInt(getFrameSlot())")
    protected int readInt(VirtualFrame frame) {
        return frame.getInt(this.getFrameSlot());
    }

    @Specialization(replaces = "readInt", guards = "frame.isDouble(getFrameSlot())")
    protected double readDouble(VirtualFrame frame) {
        return frame.getDouble(this.getFrameSlot());
    }

    @Specialization(guards = "frame.isBoolean(getFrameSlot())")
    protected boolean readBool(VirtualFrame frame) {
        return frame.getBoolean(this.getFrameSlot());
    }

    @Specialization(replaces = {"readInt", "readDouble", "readBool"})
    protected Object readObject(VirtualFrame frame) {
        System.out.println("ref->" + frame);
        System.out.println(this.getFrameSlot());
        var res = frame.getObject(this.getFrameSlot());
        if (res == null)
            res = this.currentLanguageContext().evaluatorEnvironment.closureFrame.getObject(this.getFrameSlot());
        System.out.println(res);
        return res;
    }
}
