package me.dylancurzon.pages.element;

import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Cached;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

public abstract class MutableElement {

    protected final Spacing margin;

    protected MutableContainer parent;

    private final Cached<Vector2i> cachedSize = new Cached<>();

    protected MutableElement( Spacing margin) {
        this.margin = margin;
    }

    public Spacing getMargin() {
        return margin;
    }

    public Vector2i getMarginedSize() {
        return getSize().add(
            Vector2i.of(
                margin.getLeft() + margin.getRight(),
                margin.getBottom() + margin.getTop()
            )
        );
    }

    public void setParent(MutableContainer parent) {
        this.parent = parent;
    }

    public MutableContainer getParent() {
        return parent;
    }

    public Vector2i getSize() {
        return cachedSize.get()
            .orElseGet(() -> {
                Vector2i size = calculateSize();
                cachedSize.set(size);
                return size;
            });
    }

    public abstract Vector2i calculateSize();

}
