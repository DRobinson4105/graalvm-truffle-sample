package org.example;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.js.builtins.StringPrototypeBuiltins;
import org.example.nodes.EasyScriptNode;
import org.example.nodes.exprs.*;
import org.example.nodes.roots.BuiltInFuncRootNode;
import org.example.nodes.roots.StmtBlockRootNode;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import org.example.runtime.ArrayObject;
import org.example.runtime.FunctionObject;
import org.example.runtime.MathObject;

import java.util.stream.IntStream;

@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends
        TruffleLanguage<EasyScriptLanguageContext> {
    private static final LanguageReference<EasyScriptTruffleLanguage> REF =
            LanguageReference.create(EasyScriptTruffleLanguage.class);

    public static EasyScriptTruffleLanguage get(Node node) {
        return REF.get(node);
    }

    public final Shape arrayShape = Shape.newBuilder()
            .layout(ArrayObject.class).build();

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ParsingResult parsingResult = EasyScriptTruffleParser.parse(request.getSource().getReader(), this.arrayShape);

        var programRootNode = new StmtBlockRootNode(
                this,
                parsingResult.topLevelFrameDescriptor(),
                parsingResult.programStmtBlock()
        );

        return programRootNode.getCallTarget();
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var context = new EasyScriptLanguageContext(this.createStringPrototype());

        // built-in functions
//        FunctionObject[] builtInFunctions = {
//                this.defineBuiltInFunction(AbsFunctionBodyExprNodeFactory.getInstance()),
//                this.defineBuiltInFunction(PowFunctionBodyExprNodeFactory.getInstance())
//        };
//        String[] names = {"abs", "pow"};
//
//        var objectLibrary = DynamicObjectLibrary.getUncached();
//        objectLibrary.putConstant(context.globalScopeObject, "Math", MathObject.create(this, builtInFunctions, names), 1);

        return context;
    }

    private FunctionObject defineBuiltInFunction(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        var functionArguments = IntStream
                .range(0, nodeFactory.getExecutionSignature().size())
                .mapToObj(ReadFunctionArgExprNode::new)
                .toArray(ReadFunctionArgExprNode[]::new);

        var builtInFuncRootNode = new BuiltInFuncRootNode(this,
                nodeFactory.createNode((Object) functionArguments));

        return new FunctionObject(
                builtInFuncRootNode.getCallTarget(),
                functionArguments.length,
                null,
                null
        );
    }

    private StringPrototype createStringPrototype() {
        return new StringPrototype(
                this.createCallTarget(CharAtMethodBodyExprNodeFactory.getInstance()));
    }

    private CallTarget createCallTarget(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        int argumentCount = nodeFactory.getExecutionSignature().size();
        ReadFunctionArgExprNode[] functionArguments = IntStream.range(0, argumentCount)
                .mapToObj(ReadFunctionArgExprNode::new)
                .toArray(ReadFunctionArgExprNode[]::new);

        var rootNode = new BuiltInFuncRootNode(this, nodeFactory.createNode((Object) functionArguments));

        return rootNode.getCallTarget();
    }
}