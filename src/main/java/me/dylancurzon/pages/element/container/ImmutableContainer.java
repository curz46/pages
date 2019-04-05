package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.container.stacking.MutableContainer;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ImmutableContainer extends ImmutableElement {

    @Nullable
    protected final Vector2i minimumSize;
    @Nullable
    protected final Vector2i maximumSize;

    protected ImmutableContainer(Builder builder) {
        super(builder);
        minimumSize = builder.minimumSize;
        maximumSize = builder.maximumSize;
    }

    public abstract List<ImmutableElement> getChildren();

    @Nullable
    public Vector2i getMinimumSize() {
        return minimumSize;
    }

    @Nullable
    public Vector2i getMaximumSize() {
        return maximumSize;
    }

    public static abstract class Builder<
        T extends ImmutableContainer,
        B extends Builder,
        M extends MutableContainer> extends ImmutableElement.Builder<T, B, M> {

        protected Vector2i minimumSize;
        protected Vector2i maximumSize;

        public B setMinimumSize(Vector2i minimumSize) {
            this.minimumSize = minimumSize;
            return self();
        }

        public B setMaximumSize(Vector2i maximumSize) {
            this.maximumSize = maximumSize;
            return self();
        }

        public B setFixedSize(Vector2i fixedSize) {
            minimumSize = fixedSize;
            maximumSize = fixedSize;
            return self();
        }

    }

}
