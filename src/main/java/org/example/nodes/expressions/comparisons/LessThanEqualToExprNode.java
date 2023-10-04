package org.example.nodes.expressions.comparisons;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class LessThanEqualToExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected boolean lessThanEqualToInt(int left, int right) {
        return left <= right;
    }

    @Specialization(replaces = "lessThanEqualToInt")
    protected boolean lessThanEqualToDouble(double left, double right) {
        return left <= right;
    }

    @Specialization
    public boolean lessThanEqualToString(TruffleString left, TruffleString right, @Cached TruffleString.CompareCharsUTF16Node compareNode) {
        return compareNode.execute(left, right) <= 0;
    }

    @Fallback
    protected boolean lessThanEqualToUndefined(Object left, Object right) {
        return false;
    }
}
