package org.example.nodes.expressions.arithmetic;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class DivisionExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intDivision(int leftValue, int rightValue) {
        return Math.floorDiv(leftValue, rightValue);
    }

    @Specialization(replaces = "intDivision")
    protected double doubleDivision(double leftValue, double rightValue) {
        return leftValue * rightValue;
    }

    @Fallback
    protected double undefinedDivision(
            @SuppressWarnings("unused") Object leftValue,
            @SuppressWarnings("unused") Object rightValue
    ) {
        return Double.NaN;
    }
}