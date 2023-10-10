package org.example.nodes.expressions.arithmetic;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.EasyScriptTruffleStrings;
import org.example.EasyScriptTypeSystemGen;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class AdditionExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intAddition(int leftValue, int rightValue) {
        return Math.addExact(leftValue, rightValue);
    }

    @Specialization(replaces = "intAddition")
    protected double doubleAddition(double leftValue, double rightValue) {
        return leftValue + rightValue;
    }

    @Specialization(guards = "isComplex(left) || isComplex(right)")
    protected TruffleString objectAsStringConcatenation(Object left, Object right, @Cached TruffleString.FromJavaStringNode fromJavaStringNode) {
        return EasyScriptTruffleStrings.fromJavaString(
                EasyScriptTruffleStrings.concatTwoStrings(left, right),
                fromJavaStringNode
        );
    }

    @Specialization
    public TruffleString stringConcatenation(TruffleString left, TruffleString right, @Cached TruffleString.ConcatNode concatNode) {
        return EasyScriptTruffleStrings.concat(left, right, concatNode);
    }

    @Fallback
    protected double undefinedAddition(Object leftValue, Object rightValue) {
        return Double.NaN;
    }

    protected static boolean isComplex(Object value) {
        return !(EasyScriptTypeSystemGen.isImplicitDouble(value) ||
                EasyScriptTypeSystemGen.isBoolean(value) ||
                value == Undefined.instance);
    }
}