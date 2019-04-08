package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;
import java.util.List;
import java.util.Optional;

public abstract class ImmutableContainer extends ImmutableElement {

    @Nullable
    protected final Vector2i fixedSize;
    @Nullable
    protected final Vector2i minimumSize;
    @Nullable
    protected final Vector2i maximumSize;

    protected ImmutableContainer(Builder builder) {
        super(builder);
        fixedSize = builder.fixedSize;
        minimumSize = builder.minimumSize;
        maximumSize = builder.maximumSize;
    }

    public abstract List<ImmutableElement> getChildren();

    public Optional<Vector2i> getFixedSize() {
        return Optional.ofNullable(fixedSize);
    }

    public Optional<Vector2i> getMinimumSize() {
        return Optional.ofNullable(minimumSize);
    }

    public Optional<Vector2i> getMaximumSize() {
        return Optional.ofNullable(maximumSize);
    }

    public static abstract class Builder<
        T extends ImmutableContainer,
        B extends Builder,
        M extends MutableContainer> extends ImmutableElement.Builder<T, B, M> {

        protected Vector2i fixedSize;
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
            this.fixedSize = fixedSize;
            return self();
        }

        public B fillAllocatedSize() {
            doOnCreate(element -> element.doOnSizeAllocate(event -> {
                element.setFixedSize(event.getAllocatedSize());
            }));
            return self();
        }

        public B fillParentContainer() {
            doOnCreate(element -> {
                element.setFixedSize(element.getParent() == null
                    ? Vector2i.of(0, 0)
                    : element.getParent().getSize());
            });
            return self();
        }

    }

}
