package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;
import org.example.EasyScriptTypeSystemGen;
import org.example.runtime.Undefined;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class AdditionExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return Math.addExact(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return leftValue + rightValue;
    }

    @Specialization(guards = "isComplex(left) || isComplex(right)")
    protected TruffleString concatenateComplexAsStrings(Object left, Object right, @Cached TruffleString.FromJavaStringNode fromJavaStringNode) {
        return EasyScriptTruffleStrings.fromJavaString(
                EasyScriptTruffleStrings.concatTwoStrings(left, right),
                fromJavaStringNode
        );
    }

    @Specialization
    public TruffleString addStrings(TruffleString left, TruffleString right, @Cached TruffleString.ConcatNode concatNode) {
        return EasyScriptTruffleStrings.concat(left, right, concatNode);
    }

    @Fallback
    protected double addWithUndefined(Object leftValue, Object rightValue) {
        return Double.NaN;
    }

    protected static boolean isComplex(Object value) {
        return !isPrimitive(value);
    }
    private static boolean isPrimitive(Object value) {
        return EasyScriptTypeSystemGen.isImplicitDouble(value) ||
                EasyScriptTypeSystemGen.isBoolean(value) ||
                value == Undefined.INSTANCE;
    }
}