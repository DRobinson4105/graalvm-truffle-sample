package org.example;

public record LocalVariableFrameSlotId(String variableName, int index) {

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LocalVariableFrameSlotId)) {
            return false;
        }
        var otherSlotId = (LocalVariableFrameSlotId) other;
        return this.index == otherSlotId.index &&
                this.variableName.equals(otherSlotId.variableName);
    }

    @Override
    public String toString() {
        return this.variableName + "-" + this.index;
    }
}
