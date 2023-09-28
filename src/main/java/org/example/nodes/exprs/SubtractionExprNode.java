package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class SubtractionExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int subtractInts(int leftValue, int rightValue) {
        return Math.subtractExact(leftValue, rightValue);
    }

    @Specialization(replaces = "subtractInts")
    protected double subtractDoubles(double leftValue, double rightValue) {
        return leftValue - rightValue;
    }

    @Fallback
    protected double subtractWithUndefined(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}