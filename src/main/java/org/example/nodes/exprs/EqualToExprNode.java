package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class EqualToExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected boolean intEquality(int left, int right) {
        return left == right;
    }

    @Specialization(replaces = "intEquality")
    protected boolean doubleEquality(double left, double right) {
        return left == right;
    }

    @Specialization
    protected boolean booleanEquality(boolean left, boolean right) {
        return left == right;
    }

    @Specialization
    protected boolean stringEquality(TruffleString left, TruffleString right, @Cached TruffleString.EqualNode equalNode) {
        return EasyScriptTruffleStrings.equals(left, right, equalNode);
    }

    @Fallback
    protected boolean undefinedEquality(Object left, Object right) {
        return false;
    }
}
