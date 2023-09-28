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
            Object[] argumentValues = new Object[this.callArguments.length];

            for (int i = 0; i < this.callArguments.length; i++) {
                argumentValues[i] = this.callArguments[i].executeGeneric(frame);
            }

            argumentValues = extendedArguments(argumentValues, functionObject);

            return this.dispatchNode.executeDispatch(function, argumentValues);
        }

        throw new EasyScriptException(this, "'" + function + "' is not a function");
    }

    private static Object[] extendedArguments(Object[] arguments, FunctionObject function) {
        if (arguments.length >= function.argumentCount && function.methodTarget == null) {
            return arguments;
        }
        Object[] ret = new Object[function.argumentCount];
        for (int i = 0; i < function.argumentCount; i++) {
            int j;
            if (function.methodTarget == null) {
                j = i;
            } else {
                if (i == 0) {
                    ret[0] = function.methodTarget;
                    continue;
                } else {
                    j = i - 1;
                }
            }
            ret[i] = j < arguments.length ? arguments[j] : Undefined.INSTANCE;
        }
        return ret;
    }
}
