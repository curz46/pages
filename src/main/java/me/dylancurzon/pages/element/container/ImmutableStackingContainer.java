package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImmutableStackingContainer extends ImmutableContainer {

    protected final List<ImmutableElement> children = new ArrayList<>();
    protected final Axis majorAxis;
    protected final boolean centering;

    protected ImmutableStackingContainer(AbstractBuilder builder) {
        super(builder);
        majorAxis = builder.majorAxis == null ? Axis.VERTICAL : builder.majorAxis;
        centering = builder.centering;
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
                zPosition,
                majorAxis,
                centering,
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

            if (centering && fixedSize == null) {
                throw new IllegalArgumentException(
                    "ImmutableStackingContainer cannot be centering without a fixedSize");
            }

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
        protected Axis majorAxis;
        protected boolean centering;

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

        public B setCentering(boolean centering) {
            this.centering = centering;
            return self();
        }

        public B setMajorAxis(Axis majorAxis) {
            this.majorAxis = majorAxis;
            return self();
        }

    }

}
