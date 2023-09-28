package org.example;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class ReturnException extends ControlFlowException {
    public final Object returnValue;

    public ReturnException(Object returnValue) {
        this.returnValue = returnValue;
    }
}
