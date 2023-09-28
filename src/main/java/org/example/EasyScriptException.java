package org.example;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public final class EasyScriptException extends AbstractTruffleException {
    public EasyScriptException(String message) {
        this(null, message);
    }

    public EasyScriptException(Node location, String message) {
        super(message, location);
    }
}