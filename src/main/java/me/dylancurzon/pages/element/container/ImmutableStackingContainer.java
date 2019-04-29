package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImmutableStackingContainer extends ImmutableContainer {

    protected final List<ImmutableElement> children = new ArrayList<>();
    protected final boolean centerOnX;
    protected final boolean centerOnY;

    protected ImmutableStackingContainer(AbstractBuilder builder) {
        super(builder);
        centerOnX = builder.centerOnX;
        centerOnY = builder.centerOnY;
    }

    @Override
    public List<ImmutableElement> getChildren() {
        return children;
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            MutableContainer container = new MutableStackingContainer(
                parent,
                margin,
                tag,
                zIndex,
                visible,
                majorAxis,
                centerOnX,
                centerOnY,
                fixedSize,
                minimumSize,
                maximumSize,
                decoration
            );
            List<MutableElement> mutableChildren = children.stream()
                .map(element -> element.asMutable(container))
                .collect(Collectors.toList());
            container.getChildren().addAll(mutableChildren);
            // Update positions and size
            container.propagateUpdate();

            listeners.forEach(container::subscribe);
            onCreate.forEach(consumer -> consumer.accept(container));

            return container;
        };
    }

    public static class Builder extends AbstractBuilder<ImmutableStackingContainer, Builder, MutableStackingContainer> {

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public ImmutableStackingContainer build() {
            ImmutableStackingContainer container = new ImmutableStackingContainer(this);
            container.getChildren().addAll(
                childrenFunctions.stream()
                    .map(function -> function.apply(container))
                    .collect(Collectors.toList())
            );

            return container;
        }

    }

    /**
     * An abstract version of the ImmutableStackingContainer's Builder such that
     * {@link me.dylancurzon.pages.PageTemplate.Builder} can inherit it.
     */
    protected static abstract class AbstractBuilder<
        T extends ImmutableStackingContainer,
        B extends AbstractBuilder,
        M extends MutableStackingContainer> extends ImmutableContainer.Builder<T, B, M> {

        protected final List<Function<ImmutableStackingContainer, ImmutableElement>> childrenFunctions = new ArrayList<>();
        protected boolean centerOnX;
        protected boolean centerOnY;

        public B add(ImmutableElement element) {
            childrenFunctions.add(page -> element);
            return self();
        }

        public B add(ImmutableElement... elements) {
            for (ImmutableElement element : elements) {
                add(element);
            }
            return self();
        }

        public B add(List<ImmutableElement> elements) {
            elements.forEach(this::add);
            return self();
        }

        public B add(Function<ImmutableStackingContainer, ImmutableElement> function) {
            childrenFunctions.add(function);
            return self();
        }

        public B setCenterOnX(boolean centerOnX) {
            this.centerOnX = centerOnX;
            return self();
        }

        public B setCenterOnY(boolean centerOnY) {
            this.centerOnY = centerOnY;
            return self();
        }

    }

}
