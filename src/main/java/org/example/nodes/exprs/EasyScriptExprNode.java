package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTypeSystemGen;
import org.example.nodes.EasyScriptNode;
import org.example.EasyScriptTypeSystem;
import org.example.runtime.Undefined;

@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends EasyScriptNode {
    public abstract Object executeGeneric(VirtualFrame frame);

    public boolean executeBool(VirtualFrame frame) {
        Object value = this.executeGeneric(frame);
        if (value == Undefined.INSTANCE) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Integer) {
            return (Integer) value != 0;
        }
        if (value instanceof Double) {
            return (Double) value != 0.0;
        }
        if (value instanceof TruffleString) {
            return !((TruffleString) value).isEmpty();
        }
        return true;
    }

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }
}