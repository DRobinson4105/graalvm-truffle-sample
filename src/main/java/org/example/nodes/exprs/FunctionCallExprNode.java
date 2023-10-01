package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.js.nodes.control.ReturnException;
import org.example.EasyScriptException;
import org.example.nodes.FunctionDispatchNode;
import org.example.nodes.FunctionDispatchNodeGen;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.runtime.FunctionObject;
import org.example.runtime.Undefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionCallExprNode extends EasyScriptExprNode {

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode targetFunction;

    @Children
    private final EasyScriptExprNode[] callArguments;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private FunctionDispatchNode dispatchNode;
    public FunctionCallExprNode(EasyScriptExprNode targetFunction, List<EasyScriptExprNode> callArguments) {
        super();
        this.targetFunction = targetFunction;
        this.callArguments = callArguments.toArray(new EasyScriptExprNode[]{});
        this.dispatchNode = FunctionDispatchNodeGen.create();
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object function = this.targetFunction.executeGeneric(frame);

        if (function instanceof FunctionObject functionObject) {
            Object[] argumentValues = new Object[this.callArguments.length + 1];

            argumentValues[0] = functionObject.enclosingFrame;
            for (int i = 0; i < this.callArguments.length; i++) {
                argumentValues[i + 1] = this.callArguments[i].executeGeneric(frame);
            }

            argumentValues = extendedArguments(argumentValues, functionObject);

            return this.dispatchNode.executeDispatch(function, argumentValues);
        }

        throw new EasyScriptException(this, "'" + function + "' is not a function");
    }

    private Object[] extendedArguments(Object[] arguments, FunctionObject function) {
        if (arguments.length >= function.argumentCount) {
            return arguments;
        }

        Object[] ret = new Object[function.argumentCount + 1];
        for (int i = 0; i < function.argumentCount + 1; i++) {
            ret[i + 1] = i < arguments.length ? arguments[i] : Undefined.INSTANCE;
        }
        return ret;
    }
}
