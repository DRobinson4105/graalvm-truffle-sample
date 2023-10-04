package org.example;

import com.oracle.truffle.api.frame.VirtualFrame;

public record FrameInfo(VirtualFrame enclosingFrame, int depth) {
    public FrameInfo(VirtualFrame enclosingFrame, int depth) {
        this.enclosingFrame = enclosingFrame.materialize();
        this.depth = depth;
    }
}
