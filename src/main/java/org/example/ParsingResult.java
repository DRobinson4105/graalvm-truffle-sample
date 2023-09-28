package org.example;

import com.oracle.truffle.api.frame.FrameDescriptor;
import org.example.nodes.stmts.UserFuncBodyStmtNode;

public record ParsingResult(UserFuncBodyStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor) {
}
