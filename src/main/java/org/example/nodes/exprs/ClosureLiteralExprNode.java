package org.example.nodes.exprs;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.nodes.roots.StmtBlockRootNode;
import org.example.nodes.stmts.UserFuncBodyStmtNode;
import org.example.runtime.FunctionObject;

import java.util.List;

public class ClosureLiteralExprNode extends EasyScriptExprNode {
    private FrameDescriptor frameDescriptor;
    private UserFuncBodyStmtNode funcBody;
    private int argumentCount;

    public ClosureLiteralExprNode(FrameDescriptor frameDescriptor, UserFuncBodyStmtNode funcBody, int argumentCount) {
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
        this.argumentCount = argumentCount;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var truffleLanguage = this.currentTruffleLanguage();
        var funcRootNode = new StmtBlockRootNode(truffleLanguage, frame.getFrameDescriptor(), this.funcBody);
        var callTarget = funcRootNode.getCallTarget();
        return new FunctionObject(callTarget, this.argumentCount, frame);
    }
}
