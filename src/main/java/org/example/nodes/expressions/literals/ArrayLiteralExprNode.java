package org.example.nodes.expressions.literals;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.object.Shape;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.runtime.ArrayObject;

import java.util.Arrays;
import java.util.List;

public final class ArrayLiteralExprNode extends EasyScriptExprNode {
    private final Shape arrayShape;
    @Children
    private final EasyScriptExprNode[] arrayElementExprs;

    public ArrayLiteralExprNode(Shape arrayShape, List<EasyScriptExprNode> arrayElementExprs) {
        this.arrayShape = arrayShape;
        this.arrayElementExprs = arrayElementExprs.toArray(EasyScriptExprNode[]::new);
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object[] arrayElements = Arrays.stream(arrayElementExprs)
                .map(exprNode -> exprNode.executeGeneric(frame))
                .toArray();
        return new ArrayObject(this.arrayShape, arrayElements);
    }
}
