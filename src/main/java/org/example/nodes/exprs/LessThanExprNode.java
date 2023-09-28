package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class LessThanExprNode extends EasyScriptExprNode {

    @Specialization(rewriteOn = ArithmeticException.class)
    protected boolean lessThanInt(int left, int right) {
        return left < right;
    }

    @Specialization(replaces = "lessThanInt")
    protected boolean lessThanDouble(double left, double right) {
        return left < right;
    }

    @Specialization
    public boolean lessThanString(TruffleString left, TruffleString right, @Cached TruffleString.CompareCharsUTF16Node compareNode) {
        return compareNode.execute(left, right) < 0;
    }

    @Fallback
    protected boolean lessThanUndefined(Object left, Object right) {
        return false;
    }
}
