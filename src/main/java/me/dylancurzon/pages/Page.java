package me.dylancurzon.pages;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.TickEvent;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;

public class Page extends MutableContainer {

    private final PageTemplate template;

    private Vector2i position;
    private Vector2i mousePosition = null;

    public Page(Spacing margin, String tag, PageTemplate template, List<MutableElement> elements) {
        super(margin, tag, template, elements);
        this.template = template;
    }

    public void click(Vector2i position, MouseButton button) {
        post(new MouseClickEvent(position, button));
    }

    public void tick() {
        post(new TickEvent());
    }

    public Vector2i getPosition() {
        return position;
    }

}
