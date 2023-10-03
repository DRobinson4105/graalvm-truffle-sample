package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class InequalityExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    public boolean intInequality(int left, int right) {
        return left != right;
    }

    @Specialization(replaces = "intInequality")
    public boolean doubleInequality(double left, double right) {
        return left != right;
    }

    @Specialization
    protected boolean booleanInequality(boolean left, boolean right) {
        return left != right;
    }

    @Specialization
    protected boolean stringInequality(TruffleString left, TruffleString right, @Cached TruffleString.EqualNode equalNode) {
        return !EasyScriptTruffleStrings.equals(left, right, equalNode);
    }

    @Fallback
    protected boolean objectInequality(Object left, Object right) {
        return left != right;
    }
}
