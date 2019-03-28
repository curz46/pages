package me.dylancurzon.pages;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;

public class Page extends MutableContainer {

    private final PageTemplate template;

    private Vector2i position;
    private Vector2i mousePosition = null;

    public Page(Spacing margin, PageTemplate template, List<MutableElement> elements) {
        super(margin, template, elements);
        this.template = template;
    }

    public void click(Vector2i position, MouseButton button) {
        post(new MouseClickEvent(position, button));
    }

    public Vector2i getPosition() {
        return position;
    }

}
