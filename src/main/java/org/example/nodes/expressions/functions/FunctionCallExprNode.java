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

public final class FunctionCallExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode targetFunction;
    @SuppressWarnings("FieldMayBeFinal")
    @Children private EasyScriptExprNode[] callArguments;
    @SuppressWarnings("FieldMayBeFinal")
    @Child private FunctionDispatchNode dispatchNode;

    public FunctionCallExprNode(
            EasyScriptExprNode targetFunction, List<EasyScriptExprNode> callArguments
    ) {
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
            int containsEnclosingInfo = 1;
            Object[] argumentValues = new Object[this.callArguments.length + 1];

            // set enclosing frame as the parent frame for closure frames
            if (functionObject.enclosingFrame != null)
                argumentValues[0] = functionObject.enclosingFrame;

            // add surrounding truffle object to beginning of frame for method calls
            else if (functionObject.methodTarget != null)
                argumentValues[0] = functionObject.methodTarget;

            // no enclosing info provided
            else
                containsEnclosingInfo = 0;

            for (int i = 0; i < this.callArguments.length; i++)
                argumentValues[i + containsEnclosingInfo] = this.callArguments[i].executeGeneric(frame);

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
