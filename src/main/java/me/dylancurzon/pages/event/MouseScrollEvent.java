package me.dylancurzon.pages.event;

public class MouseScrollEvent {

    private final double offset;

    public MouseScrollEvent(double offset) {
        this.offset = offset;
    }

    public double getOffset() {
        return offset;
    }

}
