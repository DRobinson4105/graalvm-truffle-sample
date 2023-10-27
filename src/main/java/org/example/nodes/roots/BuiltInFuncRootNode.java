package org.example.nodes.roots;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import org.example.EasyScriptTruffleLanguage;
import org.example.nodes.expressions.functions.builtin.BuiltInFunctionBodyExprNode;

public final class BuiltInFuncRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BuiltInFunctionBodyExprNode functionBodyExpr;

    public BuiltInFuncRootNode(
            EasyScriptTruffleLanguage truffleLanguage,
            BuiltInFunctionBodyExprNode functionBodyExpr
    ) {
        super(truffleLanguage);

        this.functionBodyExpr = functionBodyExpr;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.functionBodyExpr.executeGeneric(frame);
    }
}