package org.example.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import org.example.EasyScriptException;
import org.example.EasyScriptTruffleLanguage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ExportLibrary(InteropLibrary.class)
public final class GlobalScopeObject extends DynamicObject {
    public GlobalScopeObject(Shape shape) {
        super(shape);
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
                             @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.containsKey(this, member);
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return new MemberNamesObject(objectLibrary.getKeyArray(this));
    }

    @ExportMessage
    Object readMember(String member,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) throws UnknownIdentifierException {
        Object value = objectLibrary.getOrDefault(this, member, null);
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
                               @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.containsKey(this, member);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
                               @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return !objectLibrary.containsKey(this, member);
    }

    @ExportMessage
    void writeMember(String member, Object value,
                     @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        objectLibrary.put(this, member, value);
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return "global";
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<?>> getLanguage() {
        return EasyScriptTruffleLanguage.class;
    }
}