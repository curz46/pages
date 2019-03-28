package me.dylancurzon.pages.event;

import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2i;

public class MouseClickEvent {

    private final Vector2i position;
    private final MouseButton button;

    public MouseClickEvent(Vector2i position, MouseButton button) {
        this.position = position;
        this.button = button;
    }

    public Vector2i getPosition() {
        return position;
    }

    public MouseButton getButton() {
        return button;
    }

    @Override
    public String toString() {
        return "MouseClickEvent{" +
            "position=" + position +
            ", button=" + button + "}";
    }

}
