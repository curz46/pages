package me.dylancurzon.pages.event;

import me.dylancurzon.pages.util.Vector2i;

public class SizeAllocationEvent {

    private final Vector2i allocatedSize;

    public SizeAllocationEvent(Vector2i allocatedSize) {
        this.allocatedSize = allocatedSize;
    }

    public Vector2i getAllocatedSize() {
        return allocatedSize;
    }

    @Override
    public String toString() {
        return "SizeAllocationEvent{" +
            "allocatedSize=" + allocatedSize + "}";
    }

}
