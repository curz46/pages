package me.dylancurzon.pages.element;

import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.event.bus.SimpleEventBus;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

public abstract class MutableElement extends SimpleEventBus {

    protected final Spacing margin;
    protected MutableContainer parent;

    protected MutableElement(Spacing margin) {
        this.margin = margin;
    }

    /**
     * This method should be called whenever this {@link MutableElement} is changed in a way that could affect how
     * parent {@link MutableContainer}s present it.
     */
    public void propagateUpdate() {
        if (parent != null) {
            parent.propagateUpdate();
        }
    }

    public void setParent(MutableContainer parent) {
        this.parent = parent;
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

    public MutableContainer getParent() {
        return parent;
    }

    public abstract Vector2i getSize();

}
