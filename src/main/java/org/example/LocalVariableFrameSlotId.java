package org.example;

public record LocalVariableFrameSlotId(String variableName, int index) {
    @Override
    public boolean equals(Object other) {
        if (other instanceof LocalVariableFrameSlotId otherSlotId) {
            return this.index == otherSlotId.index &&
                    this.variableName.equals(otherSlotId.variableName);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.variableName + "-" + this.index;
    }
}
