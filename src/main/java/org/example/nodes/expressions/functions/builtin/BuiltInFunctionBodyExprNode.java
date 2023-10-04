package org.example.nodes.expressions.functions.builtin;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.expressions.functions.ReadFunctionArgExprNode;

@NodeChild(value = "arguments", type = ReadFunctionArgExprNode[].class)
@GenerateNodeFactory
public abstract class BuiltInFunctionBodyExprNode extends EasyScriptExprNode {}
