package org.example.nodes.expressions.functions.builtin;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class AbsFunctionBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intAbs(int argument) {
        return argument < 0 ? Math.negateExact(argument) : argument;
    }

    @Specialization(replaces = "intAbs")
    protected double doubleAbs(double argument) {
        return Math.abs(argument);
    }

    @Fallback
    protected double nonNumberAbs(Object argument) {
        return Double.NaN;
    }
}
