package me.dylancurzon.pages.elements.mutable;


import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.NotNull;

public abstract class WrappingMutableElement extends MutableElement {

    @NotNull
    private final MutableElement element;

    protected WrappingMutableElement(@NotNull MutableElement element) {
        super(element.getMargin(), element.getInteractOptions());
        this.element = element;
    }

    @NotNull
    public MutableElement getWrappedElement() {
        return element;
    }

    @Override
    public void tick() {
        element.tick();
    }

    @Override
    public Vector2i getSize() {
        return element.getSize();
    }

    @Override
    public Vector2i calculateSize() {
        return element.calculateSize();
    }

}
