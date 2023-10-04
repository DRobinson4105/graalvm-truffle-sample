package org.example.nodes;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import org.example.EasyScriptTruffleStrings;
import org.example.runtime.FunctionObject;
import org.example.runtime.Undefined;

@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ReadTruffleStringPropertyNode extends EasyScriptNode {
    protected static final String LENGTH_PROP = "length";
    protected static final String CHAR_AT_PROP = "charAt";

    public abstract Object executeReadTruffleStringProperty(TruffleString truffleString, Object property);

    @Specialization
    protected Object readStringIndex(
            TruffleString truffleString, int index,
            @Cached TruffleString.CodePointLengthNode lengthNode,
            @Cached TruffleString.SubstringNode substringNode) {
        return index < 0 || index >= EasyScriptTruffleStrings.length(truffleString, lengthNode)
                ? Undefined.INSTANCE
                : EasyScriptTruffleStrings.substring(truffleString, index, 1, substringNode);
    }

    @Specialization(guards = "LENGTH_PROP.equals(propertyName)")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return EasyScriptTruffleStrings.length(truffleString, lengthNode);
    }

    @Specialization(guards = {"CHAR_AT_PROP.equals(propertyName)", "same(charAtMethod.methodTarget, truffleString)"})
    protected FunctionObject readCharAtPropertyCached(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached("createCharAtMethodObject(truffleString)") FunctionObject charAtMethod) {
        return charAtMethod;
    }

    @Specialization(guards = "CHAR_AT_PROP.equals(propertyName)", replaces = "readCharAtPropertyCached")
    protected FunctionObject readCharAtPropertyUncached(
            TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName
    ) {
        return createCharAtMethodObject(truffleString);
    }

    protected FunctionObject createCharAtMethodObject(TruffleString truffleString) {
        return new FunctionObject(currentLanguageContext().stringPrototype.charAtMethod(), 2, truffleString);
    }

    @Fallback
    protected Undefined readUnknownProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") Object property
    ) {
        return Undefined.INSTANCE;
    }
}
