package org.example.runtime;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class EvaluatorEnvironment {
    public VirtualFrame closureFrame;
    public EvaluatorEnvironment() {
        closureFrame = null;
    }
}
