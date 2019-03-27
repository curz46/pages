package me.dylancurzon.pages;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;

public class Page extends MutableContainer {

    private final PageTemplate template;

    private Vector2i position;

    public Page(Spacing margin, PageTemplate template, List<MutableElement> elements) {
        super(margin, template, elements);
        this.template = template;
    }

    public Vector2i getPosition() {
        return position;
    }

}
