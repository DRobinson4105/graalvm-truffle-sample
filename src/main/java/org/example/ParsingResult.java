package org.example;

import com.oracle.truffle.api.frame.FrameDescriptor;
import org.example.nodes.statements.EasyScriptStmtNode;

public record ParsingResult(EasyScriptStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor) {}