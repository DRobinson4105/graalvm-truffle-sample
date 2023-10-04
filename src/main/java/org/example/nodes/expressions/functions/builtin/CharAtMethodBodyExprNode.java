package org.example.nodes.expressions.functions.builtin;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;
import org.example.nodes.expressions.functions.builtin.BuiltInFunctionBodyExprNode;

public abstract class CharAtMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization
    protected TruffleString charAtInt(TruffleString self, int index,
                                         @Cached @Cached.Shared("lengthNode") TruffleString.CodePointLengthNode lengthNode,
                                         @Cached @Cached.Shared("substringNode") TruffleString.SubstringNode substringNode) {
        return index < 0 || index >= EasyScriptTruffleStrings.length(self, lengthNode)
                ? EasyScriptTruffleStrings.EMPTY
                : EasyScriptTruffleStrings.substring(self, index, 1, substringNode);
    }

    @Fallback
    protected TruffleString charAtNonInt(Object self,
                                         @SuppressWarnings("unused") Object nonIntIndex,
                                         @Cached @Cached.Shared("lengthNode") TruffleString.CodePointLengthNode lengthNode,
                                         @Cached @Cached.Shared("substringNode") TruffleString.SubstringNode substringNode) {
        return this.charAtInt((TruffleString) self, 0, lengthNode, substringNode);
    }
}
