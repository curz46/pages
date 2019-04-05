package me.dylancurzon.pages.element;

import me.dylancurzon.pages.element.container.stacking.MutableContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.MouseHoverEvent;
import me.dylancurzon.pages.event.TickEvent;
import me.dylancurzon.pages.event.bus.SimpleEventBus;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class MutableElement extends SimpleEventBus {

    protected final Spacing margin;
    @Nullable
    protected final String tag;
    protected int zPosition;

    @Nullable
    protected MutableContainer parent;

    protected Vector2i mousePosition = null;

    protected MutableElement(@Nullable MutableContainer parent, Spacing margin, @Nullable String tag, @Nullable Integer zPosition) {
        this.margin = Objects.requireNonNull(margin);
        this.tag = tag;

        if (zPosition == null) {
            if (parent == null) {
                throw new IllegalArgumentException("if no parent specified, must provide a Z position");
            }

            // Add one to parent by default
            this.zPosition = parent.getZ() + 1;
        } else {
            this.zPosition = zPosition;

        }

        this.parent = parent;
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
     * Equivalent to {@code AbstractBuilder#subscribe(MouseHoverEnd.Start.class, consumer)}.
     */
    public void doOnHoverStart(Consumer<MouseHoverEvent.Start> consumer) {
        subscribe(MouseHoverEvent.Start.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseHoverEvent.End} on this {@link MutableElement}.
     * Equivalent to {@code AbstractBuilder#subscribe(MouseHoverEvent.End.class, consumer)}.
     */
    public void doOnHoverEnd(Consumer<MouseHoverEvent.End> consumer) {
        subscribe(MouseHoverEvent.End.class, consumer);
    }

    /**
     * Equivalent to {@code AbstractBuilder#subscribe(TickEvent.class, consumer)}.
     * @see ImmutableElement.Builder#subscribe(Class, Consumer)
     */
    public void doOnTick(Consumer<TickEvent> consumer) {
        subscribe(TickEvent.class, consumer);
    }

    /**
     * Equivalent to {@code AbstractBuilder#subscribe(TickEvent.class, consumer)}.
     * @see ImmutableElement.Builder#subscribe(Class, Consumer)
     */
    public void doOnTick(Runnable runnable) {
        doOnTick(event -> runnable.run());
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

    public int getZ() {
        return zPosition;
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
