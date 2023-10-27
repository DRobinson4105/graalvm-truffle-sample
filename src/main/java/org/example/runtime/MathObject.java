package org.example.runtime;

import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.staticobject.DefaultStaticProperty;
import com.oracle.truffle.api.staticobject.StaticProperty;
import org.example.EasyScriptTruffleLanguage;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.staticobject.StaticShape;

import java.util.Arrays;
import java.util.stream.Collectors;

@ExportLibrary(InteropLibrary.class)
public class MathObject implements TruffleObject {
    /**
     * Build {@link StaticShape} for math object with all build-in functions
     * @param language language that the math object is in
     * @param functions built-in functions
     * @param names names for the built-in functions
     * @return math object
     */
    public static MathObject create(
            EasyScriptTruffleLanguage language,
            FunctionObject[] functions,
            String[] names
    ) {
        StaticShape.Builder shapeBuilder = StaticShape.newBuilder(language);
        StaticProperty[] properties = Arrays.stream(names)
                .map(DefaultStaticProperty::new)
                .toArray(StaticProperty[]::new);

        for (StaticProperty property : properties)
            shapeBuilder.property(property, Object.class, true);

        Object staticObject = shapeBuilder.build().getFactory().create();

        for (int i = 0; i < functions.length; i++)
            properties[i].setObject(staticObject, functions[i]);

        return new MathObject(staticObject, properties, names);
    }

    private final Object targetObject;
    private final StaticProperty[] properties;
    private final String[] names;

    private MathObject(Object targetObject, StaticProperty[] properties, String[] names) {
        this.targetObject = targetObject;
        this.properties = properties;
        this.names = names;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return Arrays.asList(names).contains(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        for (int i = 0; i < this.names.length; i++) {
            if (member.equals(names[i])) {
                return properties[i].getObject(this.targetObject);
            }
        }

        throw UnknownIdentifierException.create(member);
    }

    @ExportMessage
    Object getMembers(boolean includeInterval) {
        return new MemberNamesObject(names);
    }
}