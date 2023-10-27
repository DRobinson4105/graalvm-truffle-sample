package org.example.nodes;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import org.example.EasyScriptException;
import org.example.runtime.FunctionObject;
import org.example.nodes.expressions.functions.FunctionCallExprNode;
import com.oracle.truffle.api.CallTarget;

/**
 * Provides specialization for {@link FunctionCallExprNode}, caching the call target
 * and inlining the function call if the call target hasn't changed
 */
public abstract class FunctionDispatchNode extends Node {
    public abstract Object executeDispatch(Object function, Object[] arguments);

    /**
     * Compares the current function's {@link CallTarget} to the cached {@link DirectCallNode#getCallTarget()} and
     * inlines the function call if so.
     * @param function an object that holds the call target and enclosing information for the
     *                 function
     * @param arguments arguments for the function call
     * @param directCallNode a cached node that inlines the {@link CallTarget} from the function
     * @return the result of the function call
     */
    @Specialization(guards = "function.callTarget == directCallNode.getCallTarget()")
    protected static Object dispatchDirectly(
            @SuppressWarnings("unused") FunctionObject function, Object[] arguments,
            @Cached("create(function.callTarget)") DirectCallNode directCallNode
    ) {
        return directCallNode.call(arguments);
    }

    /**
     * Fallback for {@link #dispatchDirectly(FunctionObject, Object[], DirectCallNode)} for
     * functions that can't be inlined
     * @param function an object that holds the call target and enclosing information for the
     *                 function
     * @param arguments arguments for the function call
     * @param indirectCallNode a cached node to execute the {@link CallTarget} from the function
     * @return the result of the function call
     */
    @Specialization(replaces = "dispatchDirectly")
    protected static Object dispatchIndirectly(
            FunctionObject function,
            Object[] arguments,
            @Cached IndirectCallNode indirectCallNode) {
        return indirectCallNode.call(function.callTarget, arguments);
    }

    @Fallback
    protected static Object targetIsNotAFunction(
            Object nonFunction,
            @SuppressWarnings("unused") Object[] arguments) {
        throw new EasyScriptException("'" + nonFunction + "' is not a function");
    }
}
