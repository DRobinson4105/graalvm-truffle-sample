package org.example;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.strings.TruffleString;
public final class EasyScriptTruffleStrings {
    private static final TruffleString.Encoding JAVA_SCRIPT_STRING_ENCODING = TruffleString.Encoding.UTF_16;
    public static final TruffleString EMPTY = JAVA_SCRIPT_STRING_ENCODING.getEmpty();

    public static TruffleString fromJavaString(String value) {
        return TruffleString.fromJavaStringUncached(value, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static TruffleString fromJavaString(String value, TruffleString.FromJavaStringNode fromJavaStringNode) {
        return fromJavaStringNode.execute(value, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static boolean equals(TruffleString string1, TruffleString string2, TruffleString.EqualNode equalNode) {
        return equalNode.execute(string1, string2, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static int length(TruffleString truffleString, TruffleString.CodePointLengthNode lengthNode) {
        return lengthNode.execute(truffleString, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static TruffleString concat(TruffleString string1, TruffleString string2, TruffleString.ConcatNode concatNode) {
        return concatNode.execute(string1, string2, JAVA_SCRIPT_STRING_ENCODING, true);
    }

    public static TruffleString substring(TruffleString truffleString, int index, int length, TruffleString.SubstringNode substringNode) {
        return substringNode.execute(truffleString, index, length, JAVA_SCRIPT_STRING_ENCODING, true);
    }

    public static boolean same(Object object1, Object object2) {
        return object1 == object2;
    }

    @CompilerDirectives.TruffleBoundary
    public static String concatTwoStrings(Object object1, Object object2) {
        return object1.toString() + object2.toString();
    }
}
