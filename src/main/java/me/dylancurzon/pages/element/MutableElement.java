package me.dylancurzon.pages.element;

import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.MouseHoverEvent;
import me.dylancurzon.pages.event.bus.SimpleEventBus;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class MutableElement extends SimpleEventBus {

    protected final Spacing margin;
    protected final String tag;

    protected MutableContainer parent;

    protected Vector2i mousePosition = null;

    protected MutableElement(Spacing margin, String tag) {
        this.margin = margin;
        this.tag = tag;
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseClickEvent} on this {@link MutableElement}.
     * Equivalent to {@code MutableElement#subscribe(MouseClickEvent.class, consumer)}.
     */
    public void doOnClick(Consumer<MouseClickEvent> event) {
        subscribe(MouseClickEvent.class, event);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseHoverEvent.Start} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(MouseHoverEnd.Start.class, consumer)}.
     */
    public void doOnHoverStart(Consumer<MouseHoverEvent.Start> consumer) {
        subscribe(MouseHoverEvent.Start.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseHoverEvent.End} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(MouseHoverEvent.End.class, consumer)}.
     */
    public void doOnHoverEnd(Consumer<MouseHoverEvent.End> consumer) {
        subscribe(MouseHoverEvent.End.class, consumer);
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

    public void setMousePosition(Vector2i mousePosition) {
        boolean previousContains = this.mousePosition != null;
        boolean nowContains = mousePosition != null;

        this.mousePosition = mousePosition;

        if (previousContains != nowContains) {
            if (mousePosition == null) {
                post(new MouseHoverEvent.End());
            } else {
                post(new MouseHoverEvent.Start(mousePosition));
            }
        }
    }

    public void setParent(MutableContainer parent) {
        this.parent = parent;
    }

    public Spacing getMargin() {
        return margin;
    }

    @Nullable
    public String getTag() {
        return tag;
    }

    public Vector2i getMarginedSize() {
        return getSize().add(
            Vector2i.of(
                margin.getLeft() + margin.getRight(),
                margin.getBottom() + margin.getTop()
            )
        );
    }

    /**
     * Get the position of the mouse relative to this {@link MutableElement}. If the mouse position is not within this
     * element's bounds, then the value will be {@code null}.
     */
    @Nullable
    public Vector2i getMousePosition() {
        return mousePosition;
    }

    public MutableContainer getParent() {
        return parent;
    }

    public abstract Vector2i getSize();

    @Override
    public String toString() {
        return "MutableElement{" +
            "tag=" + tag +
            ", margin=" + margin +
            ", parent=" + parent + "}";
    }

}
