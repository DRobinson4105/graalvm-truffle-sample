package org.example;

import com.oracle.truffle.api.frame.FrameDescriptor;
import org.example.nodes.statements.blocks.UserFuncBodyStmtNode;

public record ParsingResult(UserFuncBodyStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor) {}