package org.example.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class NotEqualToExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    public boolean NotEqualToInt(int left, int right) {
        return left != right;
    }

    @Specialization(replaces = "NotEqualToInt")
    public boolean NotEqualToDouble(double left, double right) {
        return left != right;
    }

    @Fallback
    public boolean NotEqualToUndefined(Object left, Object right) {
        return false;
    }
}
