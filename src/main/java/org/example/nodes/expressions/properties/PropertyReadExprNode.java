package org.example.nodes.expressions.properties;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.EasyScriptException;
import org.example.nodes.ReadTruffleStringPropertyNode;
import org.example.nodes.expressions.EasyScriptExprNode;

@NodeChild("target")
@NodeField(name = "propertyName", type = String.class)
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    protected abstract String getPropertyName();

    @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "1")
    protected Object readProperty(
            Object target,
            @CachedLibrary("target") InteropLibrary interopLibrary
    ) {
        try {
            return interopLibrary.readMember(target, this.getPropertyName());
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        } catch (UnknownIdentifierException e) {
            return Undefined.instance;
        }
    }

    @Specialization
    protected Object readPropertyOfString(TruffleString target,
                                          @Cached ReadTruffleStringPropertyNode readTruffleStringPropertyNode) {
        return readTruffleStringPropertyNode.executeReadTruffleStringProperty(target, this.getPropertyName());
    }

    @Specialization(guards = "interopLibrary.isNull(target)", limit = "1")
    protected Object readPropertyOfUndefined(
            @SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") @CachedLibrary("target") InteropLibrary interopLibrary
    ) {
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + this.getPropertyName() + "')");
    }

    @Fallback
    protected Object readUndefinedProperty(@SuppressWarnings("unused") Object target) {
        return Undefined.instance;
    }
}
