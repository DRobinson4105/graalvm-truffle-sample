package org.example.nodes.expressions.functions;

import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.EasyScriptException;
import org.example.nodes.FunctionDispatchNode;
import org.example.nodes.FunctionDispatchNodeGen;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.runtime.FunctionObject;
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
            int infoVal = 1;
            Object[] argumentValues = new Object[this.callArguments.length + 1];

            if (functionObject.isClosure)
                argumentValues[0] = functionObject.enclosingFrame;
            else if (functionObject.methodTarget != null)
                argumentValues[0] = functionObject.methodTarget;
            else
                infoVal = 0;

            for (int i = 0; i < this.callArguments.length; i++)
                argumentValues[i + infoVal] = this.callArguments[i].executeGeneric(frame);

            argumentValues = extendedArguments(argumentValues, functionObject);

            return this.dispatchNode.executeDispatch(function, argumentValues);
        }

        throw new EasyScriptException(this, "'" + function + "' is not a function");
    }

    private Object[] extendedArguments(Object[] arguments, FunctionObject function) {
        if (arguments.length > function.argumentCount) {
            return arguments;
        }

        Object[] ret = new Object[function.argumentCount + 1];

        int i = 0;
        for (; i < arguments.length; i++)
            ret[i] = arguments[i];
        for (; i < function.argumentCount; i++)
            ret[i] = Undefined.instance;

        return ret;
    }
}
