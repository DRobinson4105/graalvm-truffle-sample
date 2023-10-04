package org.example.nodes.expressions;

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
    public boolean executeBool(VirtualFrame frame) {
        Object value = this.executeGeneric(frame);

        if (value == Undefined.INSTANCE)
            return false;
        if (value instanceof Boolean boolValue)
            return boolValue;
        if (value instanceof Integer intValue)
            return intValue != 0;
        if (value instanceof Double doubleValue)
            return doubleValue != 0.0;
        if (value instanceof TruffleString stringValue)
            return !stringValue.isEmpty();

        return true;
    }

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }

    public abstract Object executeGeneric(VirtualFrame frame);
}