package org.example.nodes.expressions.arithmetic;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class MultiplicationExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intMultiplication(int leftValue, int rightValue) {
        return Math.multiplyExact(leftValue, rightValue);
    }

    @Specialization(replaces = "intMultiplication")
    protected double doubleMultiplication(double leftValue, double rightValue) {
        return leftValue * rightValue;
    }

    @Fallback
    protected double undefinedMultiplication(
            @SuppressWarnings("unused") Object leftValue,
            @SuppressWarnings("unused") Object rightValue
    ) {
        return Double.NaN;
    }
}