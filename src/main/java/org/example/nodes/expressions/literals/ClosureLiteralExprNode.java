package org.example.nodes.expressions.literals;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.roots.StmtBlockRootNode;
import org.example.nodes.statements.blocks.FuncBodyStmtNode;
import org.example.runtime.FunctionObject;

public final class ClosureLiteralExprNode extends EasyScriptExprNode {
    private final FrameDescriptor frameDescriptor;
    private final FuncBodyStmtNode funcBody;
    private final int argumentCount;

    public ClosureLiteralExprNode(FrameDescriptor frameDescriptor, FuncBodyStmtNode funcBody, int argumentCount) {
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
        this.argumentCount = argumentCount;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var truffleLanguage = this.currentTruffleLanguage();
        var funcRootNode = new StmtBlockRootNode(truffleLanguage, this.frameDescriptor, this.funcBody);
        var callTarget = funcRootNode.getCallTarget();
        return new FunctionObject(callTarget, frame, this.argumentCount);
    }
}
