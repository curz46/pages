package me.dylancurzon.pages;

import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;
import java.util.Map;

public class Page extends MutableContainer {

    private final PageTemplate template;
    private final MutableContainer container;

    private Vector2i position;

    protected Page(PageTemplate template, MutableContainer container) {
        super(template.getMargin(), template, container.getElements());
        this.template = template;
        this.container = container;

        position = this.template.getPosition();
    }

    @Override
    public Vector2i calculateSize() {
        return template.getSize();
    }

    @Override
    public List<AlignedElement> draw() {
        return container.draw();
    }

    public Map<MutableElement, Vector2i> getPositions() {
        return container.getPositions();
    }

}
