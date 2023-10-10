package org.example.nodes.expressions;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptException;
import org.example.EasyScriptTruffleStrings;
import org.example.nodes.expressions.functions.ReadFunctionArgExprNode;
import org.example.nodes.expressions.functions.builtin.AbsFunctionBodyExprNodeFactory;
import org.example.nodes.expressions.functions.builtin.BuiltInFunctionBodyExprNode;
import org.example.nodes.expressions.functions.builtin.PowFunctionBodyExprNodeFactory;
import org.example.nodes.roots.BuiltInFuncRootNode;
import org.example.runtime.FunctionObject;
import org.example.runtime.MathObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.IntStream;

public final class RequireExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode textExpr;
    public RequireExprNode(EasyScriptExprNode textExpr) {
        this.textExpr = textExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        String text = textExpr.executeGeneric(frame).toString();
        return switch(text) {
            case "Math" -> getMathObject(frame);
            default -> throw new EasyScriptException(this, "Library does not exist");
        };
    }

    private Object getMathObject(VirtualFrame frame) {
        FunctionObject[] builtInFunctions = {
                this.defineBuiltInFunction(AbsFunctionBodyExprNodeFactory.getInstance()),
                this.defineBuiltInFunction(PowFunctionBodyExprNodeFactory.getInstance())
        };
        String[] names = {"abs", "pow"};

        return MathObject.create(this.currentTruffleLanguage(), builtInFunctions, names);
    }

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
