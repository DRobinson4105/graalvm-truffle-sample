package org.example.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.js.runtime.objects.Undefined;

@ExportLibrary(InteropLibrary.class)
public class ArrayObject extends DynamicObject {
    @SuppressWarnings("unused")
    @DynamicField private long length;
    private Object[] arrayElements;
    private final String[] methodNames = new String[]{"length"};

    public ArrayObject(Shape arrayShape, Object[] arrayElements) {
        super(arrayShape);
        this.setArrayElements(arrayElements, DynamicObjectLibrary.getUncached());
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return this.arrayElements.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < this.arrayElements.length;
    }

    @ExportMessage
    boolean isArrayElementModifiable(long index) {
        return this.isArrayElementReadable(index);
    }

    @ExportMessage
    boolean isArrayElementInsertable(long index) {
        return index >= this.arrayElements.length;
    }

    @ExportMessage
    Object readArrayElement(long index) {
        return isArrayElementReadable(index)
                ? this.arrayElements[(int) index]
                : Undefined.instance;
    }

    @ExportMessage
    void writeArrayElement(long index, Object value,
                           @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        if (this.isArrayElementModifiable(index)) {
            this.arrayElements[(int) index] = value;
        } else {
            Object[] newArrayElements = new Object[(int) index + 1];
            for (int i = 0; i < index; i++) {
                newArrayElements[i] = i < this.arrayElements.length
                        ? this.arrayElements[i]
                        : Undefined.instance;
            }
            newArrayElements[(int) index] = value;
            this.setArrayElements(newArrayElements, objectLibrary);
        }
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return "length".equals(member);
    }

    @ExportMessage
    Object readMember(
            String member,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary
    ) throws UnknownIdentifierException {
        for (String method : this.methodNames){
            if (member.equals(method))
                return objectLibrary.getOrDefault(this, "length", 0);
        }

        throw UnknownIdentifierException.create(member);
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(methodNames);
    }

    private void setArrayElements(Object[] arrayElements, DynamicObjectLibrary objectLibrary) {
        this.arrayElements = arrayElements;
        for (String method : this.methodNames) {
            objectLibrary.put(this, method, arrayElements.length);
        }
    }
}