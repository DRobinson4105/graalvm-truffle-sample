package org.example.nodes.expressions.functions.builtin;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.EasyScriptException;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.expressions.functions.ReadFunctionArgExprNode;
import org.example.nodes.expressions.functions.builtin.AbsFunctionBodyExprNodeFactory;
import org.example.nodes.expressions.functions.builtin.BuiltInFunctionBodyExprNode;
import org.example.nodes.expressions.functions.builtin.PowFunctionBodyExprNodeFactory;
import org.example.nodes.roots.BuiltInFuncRootNode;
import org.example.runtime.FunctionObject;
import org.example.runtime.MathObject;
import java.util.stream.IntStream;

public final class RequireExprNode extends EasyScriptExprNode {
    /**
     * The name of the requested library
     */
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode textExpr;
    public RequireExprNode(EasyScriptExprNode textExpr) {
        this.textExpr = textExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        String text = textExpr.executeGeneric(frame).toString();

        if (text.equals("Math"))
            return getMathObject();
        else
            throw new EasyScriptException(this, "Library does not exist");
    }

    /**
     * Creates a truffle object with all the built-in math functions
     * @return the math object
     */
    private Object getMathObject() {
        FunctionObject[] builtInFunctions = {
                this.defineBuiltInFunction(AbsFunctionBodyExprNodeFactory.getInstance()),
                this.defineBuiltInFunction(PowFunctionBodyExprNodeFactory.getInstance())
        };
        String[] names = {"abs", "pow"};

        return MathObject.create(this.currentTruffleLanguage(), builtInFunctions, names);
    }

    /**
     * Uses the node factory to create a function object
     */
    private FunctionObject defineBuiltInFunction(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        var functionArguments = IntStream
                .range(0, nodeFactory.getExecutionSignature().size())
                .mapToObj(ReadFunctionArgExprNode::new)
                .toArray(ReadFunctionArgExprNode[]::new);

        var builtInFuncRootNode = new BuiltInFuncRootNode(this.currentTruffleLanguage(),
                nodeFactory.createNode((Object) functionArguments));

        return new FunctionObject(
                builtInFuncRootNode.getCallTarget(),
                functionArguments.length
        );
    }
}
