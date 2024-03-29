package org.example.nodes.expressions.comparisons;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;
import org.example.nodes.expressions.EasyScriptExprNode;
import com.oracle.truffle.api.nodes.Node;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class EqualityExprNode extends EasyScriptExprNode {
    @Specialization
    protected boolean intEquality(int left, int right) {
        return left == right;
    }

    @Specialization
    protected boolean doubleEquality(double left, double right) {
        return left == right;
    }

    @Specialization
    protected boolean booleanEquality(boolean left, boolean right) {
        return left == right;
    }

    @Specialization
    protected boolean stringEquality(
            TruffleString left, TruffleString right, @Cached TruffleString.EqualNode equalNode
    ) {
        return EasyScriptTruffleStrings.equals(left, right, equalNode);
    }

    @Fallback
    protected boolean objectEquality(Object left, Object right) {
        return left == right;
    }
}
