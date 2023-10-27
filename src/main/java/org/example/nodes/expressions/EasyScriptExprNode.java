package org.example.nodes.expressions;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.EasyScriptTypeSystemGen;
import org.example.nodes.EasyScriptNode;
import org.example.EasyScriptTypeSystem;

@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends EasyScriptNode {
    /**
     * Converts the result of {@link #executeGeneric(VirtualFrame)} to a boolean as a truthy value.
     * A node can override this method if it has a better way to producing a value of type bool.
     *
     * @param frame the enclosing frame of the current node
     * @return the value of the execution as a boolean
     */
    public boolean executeBool(VirtualFrame frame) {
        Object value = this.executeGeneric(frame);

        if (value == Undefined.instance)
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

    /**
     * Attempts to convert the result of {@link #executeGeneric(VirtualFrame)} A node can override
     * this method if it has a better way to producing a value of type int.
     *
     * @param frame the enclosing frame of the current node
     * @return the value of the execution as an int
     * @throws UnexpectedResultException if a loss-free conversion of the result to int is not possible
     */
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }

    /**
     * Executes this node using the specified context and frame and returns the result value.
     * @param frame the enclosing frame of the current node
     * @return the value of the execution
     */
    public abstract Object executeGeneric(VirtualFrame frame);
}