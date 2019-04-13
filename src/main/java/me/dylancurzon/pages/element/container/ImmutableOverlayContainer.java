package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImmutableOverlayContainer extends ImmutableContainer {

    protected final List<ImmutableElement> children = new ArrayList<>();
    protected final boolean centerOnX;
    protected final boolean centerOnY;

    protected ImmutableOverlayContainer(Builder builder) {
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
            MutableContainer container = new MutableOverlayContainer(
                parent,
                margin,
                tag,
                zIndex,
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

    /**
     * An abstract version of the ImmutableStackingContainer's Builder such that
     * {@link me.dylancurzon.pages.PageTemplate.Builder} can inherit it.
     */
    public static class Builder extends ImmutableContainer.Builder<ImmutableOverlayContainer, Builder, MutableOverlayContainer> {

        protected final List<Function<ImmutableOverlayContainer, ImmutableElement>> childrenFunctions = new ArrayList<>();
        protected boolean centerOnX;
        protected boolean centerOnY;

        public Builder add(ImmutableElement element) {
            childrenFunctions.add(page -> element);
            return self();
        }

        public Builder add(ImmutableElement... elements) {
            for (ImmutableElement element : elements) {
                add(element);
            }
            return self();
        }

        public Builder add(List<ImmutableElement> elements) {
            elements.forEach(this::add);
            return self();
        }

        public Builder add(Function<ImmutableOverlayContainer, ImmutableElement> function) {
            childrenFunctions.add(function);
            return self();
        }

        public Builder setCenterOnX(boolean centerOnX) {
            this.centerOnX = centerOnX;
            return self();
        }

        public Builder setCenterOnY(boolean centerOnY) {
            this.centerOnY = centerOnY;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public ImmutableOverlayContainer build() {
            ImmutableOverlayContainer container = new ImmutableOverlayContainer(this);
            container.getChildren().addAll(
                childrenFunctions.stream()
                    .map(function -> function.apply(container))
                    .collect(Collectors.toList())
            );

            return container;
        }

    }

}
