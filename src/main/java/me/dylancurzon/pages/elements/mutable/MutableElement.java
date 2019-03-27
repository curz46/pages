package me.dylancurzon.pages.elements.mutable;

import me.dylancurzon.pages.util.Cached;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.NotNull;

public abstract class MutableElement {

    @NotNull
    protected final Spacing margin;

    protected MutableContainer parent;

    private final Cached<Vector2i> cachedSize = new Cached<>();

    protected MutableElement(@NotNull Spacing margin) {
        this.margin = margin;
    }

    @NotNull
    public Spacing getMargin() {
        return margin;
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
