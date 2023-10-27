package org.example.nodes.expressions.comparisons;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class LessThanExprNode extends EasyScriptExprNode {
    @Specialization
    protected boolean lessThanInt(int left, int right) {
        return left < right;
    }

    @Specialization
    protected boolean lessThanDouble(double left, double right) {
        return left < right;
    }

    @Specialization
    public boolean lessThanString(
            TruffleString left, TruffleString right,
            @Cached TruffleString.CompareCharsUTF16Node compareNode
    ) {
        return compareNode.execute(left, right) < 0;
    }

    @Fallback
    protected boolean lessThanUndefined(
            @SuppressWarnings("unused") Object left, @SuppressWarnings("unused") Object right
    ) {
        return false;
    }
}
