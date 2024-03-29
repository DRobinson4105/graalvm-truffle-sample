package org.example.nodes.expressions.arrays;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.EasyScriptException;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.runtime.ArrayObject;

@NodeChild("arrayExpr")
@NodeChild("indexExpr")
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    @Specialization(guards = "arrayInteropLibrary.isArrayElementReadable(array, index)", limit = "1")
    protected Object readIntIndex(
            Object array, int index,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary
    ) {
        try {
            return arrayInteropLibrary.readArrayElement(array, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    @Specialization(guards = "interopLibrary.isNull(target)", limit = "1")
    protected Object invalidIndex(
            @SuppressWarnings("unused") Object target, Object index,
            @SuppressWarnings("unused") @CachedLibrary("target") InteropLibrary interopLibrary
    ) {
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + index + "')");
    }

    @Fallback
    protected Object invalidArray(
            @SuppressWarnings("unused") Object array, @SuppressWarnings("unused") Object index
    ) {
        return Undefined.instance;
    }
}
