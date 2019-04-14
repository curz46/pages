package me.dylancurzon.pages.element;

import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.event.*;
import me.dylancurzon.pages.event.bus.QueuingEventBus;
import me.dylancurzon.pages.event.bus.SimpleEventBus;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class MutableElement extends QueuingEventBus {

    protected final Spacing margin;
    @Nullable
    protected final String tag;
    protected int zIndex;
    protected boolean visible;

    @Nullable
    protected MutableContainer parent;

    protected ElementDecoration decoration;

    protected Vector2i mousePosition;
    @Nullable
    protected Vector2i allocatedSize;

    protected MutableElement(@Nullable MutableContainer parent,
                             Spacing margin,
                             @Nullable String tag,
                             @Nullable Integer zIndex,
                             boolean visible,
                             ElementDecoration decoration) {
        this.margin = Objects.requireNonNull(margin);
        this.tag = tag;

        if (zIndex == null) {
            if (parent == null) {
                throw new IllegalArgumentException("if no parent specified, must provide a Z position");
            }

            // Add one to parent by default
            this.zIndex = parent.getZIndex() + 1;
        } else {
            this.zIndex = zIndex;
        }
        this.visible = visible;

        this.decoration = Objects.requireNonNull(decoration);
        this.parent = parent;

        doOnTick(event -> {
            // Whenever this element experiences a tick, consume all the events that were queued.
            consume();
        });
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link UpdateEvent} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(UpdateEvent.class, consumer)}.
     */
    public void doOnUpdate(Consumer<UpdateEvent> consumer) {
        subscribe(UpdateEvent.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link UpdateEvent} on this {@link MutableElement}.
     * @see this#doOnUpdate(Consumer)
     */
    public void doOnUpdate(Runnable runnable) {
        doOnUpdate(event -> runnable.run());
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseClickEvent} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(MouseClickEvent.class, consumer)}.
     */
    public void doOnClick(Consumer<MouseClickEvent> event) {
        subscribe(MouseClickEvent.class, event);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseHoverEvent.Start} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(MouseHoverEvent.Start.class, consumer)}.
     */
    public void doOnHoverStart(Consumer<MouseHoverEvent.Start> consumer) {
        subscribe(MouseHoverEvent.Start.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseHoverEvent.Move} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(MouseHoverEvent.Move.class, consumer)}.
     */
    public void doOnHoverMove(Consumer<MouseHoverEvent.Move> consumer) {
        subscribe(MouseHoverEvent.Move.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link MouseHoverEvent.End} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(MouseHoverEvent.End.class, consumer)}.
     */
    public void doOnHoverEnd(Consumer<MouseHoverEvent.End> consumer) {
        subscribe(MouseHoverEvent.End.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link TickEvent} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(TickEvent.class, consumer)}.
     */
    public void doOnTick(Consumer<TickEvent> consumer) {
        subscribe(TickEvent.class, consumer);
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link TickEvent} on this {@link MutableElement}.
     * @see this#doOnTick(Consumer)
     */
    public void doOnTick(Runnable runnable) {
        doOnTick(event -> runnable.run());
    }

    /**
     * Subscribes the given {@link Consumer} to the {@link SizeAllocationEvent} on this {@link MutableElement}.
     * Equivalent to {@code Builder#subscribe(SizeAllocationEvent.class, consumer)}.
     */
    public void doOnSizeAllocate(Consumer<SizeAllocationEvent> consumer) {
        subscribe(SizeAllocationEvent.class, consumer);
    }

    /**
     * This method should be called whenever this {@link MutableElement} is changed in a way that could affect how
     * parent {@link MutableContainer}s present it.
     */
    public void propagateUpdate() {
        if (parent != null) {
            parent.propagateUpdate();
        }

        post(new UpdateEvent());
    }

    public void setDecoration(ElementDecoration decoration) {
        this.decoration = Objects.requireNonNull(decoration);
    }

    public void setMousePosition(Vector2i mousePosition) {
        boolean previousContains = this.mousePosition != null;
        boolean nowContains = mousePosition != null;

        if (previousContains != nowContains) {
            if (mousePosition == null) {
                post(new MouseHoverEvent.End());
            } else {
                post(new MouseHoverEvent.Start(mousePosition));
            }
        } else if (!Objects.equals(this.mousePosition, mousePosition)) {
            post(new MouseHoverEvent.Move(mousePosition));
        }

        this.mousePosition = mousePosition;
    }

    public void setParent(MutableContainer parent) {
        this.parent = parent;
    }

    // TODO: This method in particular is extremely hacky.
    public void setAllocatedSize(Vector2i allocatedSize) {
        if (Objects.equals(this.allocatedSize, allocatedSize)) {
            // Avoid a cyclical loop where the fixedSize is based on the allocatedSize
            return;
        }

        this.allocatedSize = Objects.requireNonNull(allocatedSize);
        // Post to listeners on this element
        // This has to be non-immediate, because otherwise newer positions will be evaluated during the evaluation
        // that called this method.  Newer positions will therefore be overridden by the original position evaluation
        // when it finishes, which is undesirable behaviour.
        post(new SizeAllocationEvent(allocatedSize), false);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Spacing getMargin() {
        return margin;
    }

    @Nullable
    public String getTag() {
        return tag;
    }

    public int getZIndex() {
        return zIndex;
    }

    public ElementDecoration getDecoration() {
        return decoration;
    }

    public boolean isVisible() {
        return visible && (parent == null || parent.isVisible());
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
            ", parent=" + (parent == null ? null : parent.getClass().getSimpleName()) + "}";
    }

}
