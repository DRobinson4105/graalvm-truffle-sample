package org.example.nodes.expressions.arrays;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import org.example.EasyScriptException;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.runtime.ArrayObject;

@NodeChild("arrayExpr")
@NodeChild("indexExpr")
@NodeChild("rValueExpr")
public abstract class ArrayIndexWriteExprNode extends EasyScriptExprNode {
    @Specialization(guards = "arrayInteropLibrary.isArrayElementWritable(array, index)", limit = "1")
    protected Object writeIntIndex(
            Object array, int index, Object rValue,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary
    ) {
        try {
            arrayInteropLibrary.writeArrayElement(array, index, rValue);
        } catch (UnsupportedMessageException | InvalidArrayIndexException | UnsupportedTypeException e) {
            throw new EasyScriptException(this, e.getMessage());
        }

        return rValue;
    }

    @Specialization(guards = "interopLibrary.isNull(target)", limit = "1")
    protected Object indexUndefined(
            @SuppressWarnings("unused") Object target, Object index,
            @SuppressWarnings("unused") Object rValue,
            @SuppressWarnings("unused") @CachedLibrary("target") InteropLibrary interopLibrary
    ) {
        throw new EasyScriptException("Cannot read properties of undefined (setting '" + index + "')");
    }

    @Fallback
    protected Object invalidArrayOrIndex(
            @SuppressWarnings("unused") Object array,
            @SuppressWarnings("unused") Object index, Object rValue
    ) {
        return rValue;
    }
}
