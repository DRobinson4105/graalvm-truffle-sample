package org.example.nodes.expressions.arithmetic;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("node")
public abstract class NegationExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intNegation(int value) {
        return Math.negateExact(value);
    }

    @Specialization(replaces = "intNegation")
    protected double doubleNegation(double value) {
        return -value;
    }

    @Fallback
    protected double undefinedNegation(@SuppressWarnings("unused") Object value) {
        return Double.NaN;
    }
}
