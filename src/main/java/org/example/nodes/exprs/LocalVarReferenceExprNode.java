package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.EasyScriptException;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

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
        Object res;
        while (frame != null) {
            res = frame.getObject(this.getFrameSlot());
            if (res != null)
                return res;

            if (frame.getArguments().length == 0)
                throw new EasyScriptException(this, "this variable is not defined");
            frame = (VirtualFrame) frame.getArguments()[0];
        }

        throw new EasyScriptException(this, "this variable is not defined");
    }

//    private Object findValue(VirtualFrame frame, Function<VirtualFrame, Object> valueReceiver) {
//        var res = valueReceiver.apply(frame);
//        if (res != null)
//            return res;
//
//        if (frame.getArguments().length == 0)
//            throw new EasyScriptException(this, "this variable is not defined");
//
//        return findValue((VirtualFrame) frame.getArguments()[0], valueReceiver);
//    }
}
