package org.example.nodes.roots;

import org.example.EasyScriptTruffleLanguage;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import org.example.nodes.statements.EasyScriptStmtNode;

public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode blockStmt;

    public StmtBlockRootNode(
            EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, EasyScriptStmtNode blockStmt
    ) {
        super(truffleLanguage, frameDescriptor);
        this.blockStmt = blockStmt;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }
}
