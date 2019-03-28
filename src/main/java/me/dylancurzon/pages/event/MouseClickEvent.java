package me.dylancurzon.pages.event;

import me.dylancurzon.pages.util.Vector2i;

public class MouseClickEvent {

    private final Vector2i position;
    private final Button button;

    public MouseClickEvent(Vector2i position, Button button) {
        this.position = position;
        this.button = button;
    }

    public Vector2i getPosition() {
        return position;
    }

    public Button getButton() {
        return button;
    }

    @Override
    public String toString() {
        return "MouseClickEvent{" +
            "position=" + position +
            ", button=" + button + "}";
    }

    public enum Button {

        LEFT_MOUSE_BUTTON,
        MIDDLE_MOUSE_BUTTON,
        RIGHT_MOUSE_BUTTON

    }

}
