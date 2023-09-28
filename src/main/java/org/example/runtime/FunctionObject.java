package org.example.runtime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import org.example.EasyScriptException;
import org.example.EasyScriptTypeSystemGen;
import org.example.nodes.FunctionDispatchNode;
import org.example.nodes.FunctionDispatchNodeGen;

@ExportLibrary(InteropLibrary.class)
public final class FunctionObject implements TruffleObject {
    private final FunctionDispatchNode functionDispatchNode;
    public CallTarget callTarget;
    public int argumentCount;
    public final Object methodTarget;
    public final VirtualFrame parentFrame;

    public FunctionObject(CallTarget callTarget, int argumentCount, VirtualFrame parentFrame) {
        this(callTarget, argumentCount, null, parentFrame);
    }
    public FunctionObject(CallTarget callTarget, int argumentCount, Object methodTarget, VirtualFrame parentFrame) {
        this.functionDispatchNode = FunctionDispatchNodeGen.create();
        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
        this.methodTarget = methodTarget;
        this.parentFrame = parentFrame;
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    Object execute(Object[] arguments) {
        for (Object argument : arguments) {
            if (!this.isEasyScriptValue(argument)) {
                throw new EasyScriptException("'" + argument + "' is not an EasyScript value");
            }
        }
        return this.functionDispatchNode.executeDispatch(this, arguments);
    }

    private boolean isEasyScriptValue(Object value) {
        return EasyScriptTypeSystemGen.isImplicitDouble(value) ||
                EasyScriptTypeSystemGen.isBoolean(value) ||
                value == Undefined.INSTANCE ||
                value instanceof ArrayObject ||
                value instanceof FunctionObject;
    }
}
