package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class GreaterThanExprNode extends EasyScriptExprNode {

    @Specialization(rewriteOn = ArithmeticException.class)
    protected boolean greaterThanInt(int left, int right) {
        return left > right;
    }

    @Specialization(replaces = "greaterThanInt")
    protected boolean greaterThanDouble(double left, double right) {
        return left > right;
    }

    @Specialization
    protected boolean greaterThanString(TruffleString left, TruffleString right, @Cached TruffleString.CompareCharsUTF16Node compareNode) {
        return compareNode.execute(left, right) > 0;
    }

    @Fallback
    protected boolean greaterThanUndefined(Object left, Object right) {
        return false;
    }
}