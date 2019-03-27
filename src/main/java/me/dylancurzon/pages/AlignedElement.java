package me.dylancurzon.pages;

import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.util.Vector2i;

public class AlignedElement {

    private final MutableElement element;
    private final Vector2i position;

    public AlignedElement(MutableElement element, Vector2i position) {
        this.element = element;
        this.position = position;
    }

    public MutableElement getElement() {
        return element;
    }

    public Vector2i getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "AlignedElement{" +
            "element=" + element.toString() +
            ", position=" + position.toString() +
            "}";
    }

}
