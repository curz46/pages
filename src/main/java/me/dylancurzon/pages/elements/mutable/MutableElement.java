package me.dylancurzon.pages.elements.mutable;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.util.Cached;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.util.Spacing;

import java.util.function.Consumer;

public abstract class MutableElement {

    @NotNull
    protected final Spacing margin;
    @NotNull
    protected final InteractOptions interactOptions;

    protected MutableContainer parent;

    private final Cached<Vector2i> cachedSize = new Cached<>();

    protected MutableElement(@NotNull Spacing margin, InteractOptions interactOptions) {
        this.margin = margin;
        this.interactOptions = interactOptions;
    }

    @NotNull
    public Spacing getMargin() {
        return margin;
    }

    @NotNull
    public InteractOptions getInteractOptions() {
        return interactOptions;
    }

    @NotNull
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

    /**
     * Clicks on this element.
     * Note: if this {@link MutableElement} is a {@link MutableContainer}, it will also propagate the click event
     * through the hierarchy, such that any MutableElements it is responsible for rendering are able to handle it
     * themselves.
     * @param position A position relative to this MutableElement, such that the top-left corner of this element's
     *                 rendering bounds are (0, 0).
     */
    public void click(@NotNull Vector2i position) {
        if (position.getX() < 0 || position.getX() >= getSize().getX() ||
            position.getY() < 0 || position.getY() >= getSize().getY()) {
            return;
        }
        Consumer<MutableElement> consumer = interactOptions.getClickConsumer();
        if (consumer == null) {
            return;
        }
        // fire click
        consumer.accept(this);
    }

    public Vector2i getMousePosition(MutableElement element) {
        return parent.getMousePosition(element);
    }

    public Vector2i getMousePosition() {
        if (parent == null) return null;
        return parent.getMousePosition(this);
    }

    public void tick() {}

    public Vector2i getSize() {
        return cachedSize.get()
            .orElseGet(() -> {
                Vector2i size = calculateSize();
                cachedSize.set(size);
                return size;
            });
    }

    @NotNull
    public abstract Vector2i calculateSize();

}
