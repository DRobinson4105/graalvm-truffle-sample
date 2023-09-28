package org.example.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;

public final class StringLiteralExprNode extends EasyScriptExprNode {
    private final TruffleString value;

    public StringLiteralExprNode(String value) {
        this.value = EasyScriptTruffleStrings.fromJavaString(value);
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return !this.value.isEmpty();
    }

    @Override
    public TruffleString executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}