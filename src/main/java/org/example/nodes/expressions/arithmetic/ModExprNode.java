package org.example.nodes.expressions.arithmetic;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class ModExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intMod(int leftValue, int rightValue) {
        return Math.floorMod(leftValue, rightValue);
    }

    @Specialization(replaces = "intMod")
    protected double doubleMod(double leftValue, double rightValue) {
        return leftValue % rightValue;
    }

    @Fallback
    protected double undefinedMod(
            @SuppressWarnings("unused") Object leftValue,
            @SuppressWarnings("unused") Object rightValue
    ) {
        return Double.NaN;
    }
}