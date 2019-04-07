package me.dylancurzon.pages.event;

import me.dylancurzon.pages.util.Vector2i;

public abstract class MouseHoverEvent {

    public static class Start extends MouseHoverEvent {

        private final Vector2i position;

        public Start(Vector2i position) {
            this.position = position;
        }

        public Vector2i getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return "MouseHouseEvent.Start{position=" + position + "}";
        }

    }

    public static class Move extends MouseHoverEvent {

        private final Vector2i position;

        public Move(Vector2i position) {
            this.position = position;
        }

        public Vector2i getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return "MouseHouseEvent.Move{position=" + position + "}";
        }

    }

    public static class End extends MouseHoverEvent {

        @Override
        public String toString() {
            return "MouseHoverEvent.End{}";
        }

    }

}
