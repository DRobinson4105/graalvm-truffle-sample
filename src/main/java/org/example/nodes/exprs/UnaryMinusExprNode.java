package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChild("node")
public abstract class UnaryMinusExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int negateInt(int value) {
        return Math.negateExact(value);
    }

    @Specialization(replaces = "negateInt")
    protected double negateDouble(double value) {
        return -value;
    }

    @Fallback
    protected double negateUndefined(Object value) {
        return Double.NaN;
    }
}
