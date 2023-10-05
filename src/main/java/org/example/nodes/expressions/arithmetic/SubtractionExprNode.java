package org.example.nodes.expressions.arithmetic;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class SubtractionExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intSubtraction(int leftValue, int rightValue) {
        return Math.subtractExact(leftValue, rightValue);
    }

    @Specialization(replaces = "intSubtraction")
    protected double doubleSubtraction(double leftValue, double rightValue) {
        return leftValue - rightValue;
    }

    @Fallback
    protected double undefinedSubtraction(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}