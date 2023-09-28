package org.example.nodes.roots;

import org.example.EasyScriptTruffleLanguage;
import org.example.nodes.stmts.BlockStmtNode;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import org.example.nodes.stmts.EasyScriptStmtNode;
import org.example.nodes.stmts.UserFuncBodyStmtNode;

public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode blockStmt;

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
                             FrameDescriptor frameDescriptor, BlockStmtNode blockStmt) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt);
    }

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
                             FrameDescriptor frameDescriptor, UserFuncBodyStmtNode blockStmt) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt);
    }

    private StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
                              FrameDescriptor frameDescriptor, EasyScriptStmtNode blockStmt) {
        super(truffleLanguage, frameDescriptor);

        this.blockStmt = blockStmt;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }
}
