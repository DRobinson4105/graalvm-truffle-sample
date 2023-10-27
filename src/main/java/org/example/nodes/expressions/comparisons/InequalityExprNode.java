package org.example.nodes.expressions.comparisons;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class InequalityExprNode extends EasyScriptExprNode {
    @Specialization
    public boolean intInequality(int left, int right) {
        return left != right;
    }

    @Specialization
    public boolean doubleInequality(double left, double right) {
        return left != right;
    }

    @Specialization
    protected boolean booleanInequality(boolean left, boolean right) {
        return left != right;
    }

    @Specialization
    protected boolean stringInequality(
            TruffleString left, TruffleString right, @Cached TruffleString.EqualNode equalNode
    ) {
        return !EasyScriptTruffleStrings.equals(left, right, equalNode);
    }

    @Fallback
    protected boolean objectInequality(Object left, Object right) {
        return left != right;
    }
}
