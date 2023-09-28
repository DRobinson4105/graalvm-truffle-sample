package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class PowFunctionBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization(guards = "exponent >= 0", rewriteOn = ArithmeticException.class)
    protected int intPow(int base, int exponent) {
        int ret = 1;

        for (int i = 0; i < exponent; i++) {
            ret = Math.multiplyExact(ret, base);
        }

        return ret;
    }

    @Specialization(replaces = "intPow")
    protected double doublePow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    @Fallback
    protected double nonNumberPow(Object base, Object exponent) {
        return Double.NaN;
    }
}
