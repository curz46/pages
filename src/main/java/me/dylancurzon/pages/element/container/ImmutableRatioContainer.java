package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.stacking.Axis;
import me.dylancurzon.pages.element.container.stacking.MutableContainer;
import me.dylancurzon.pages.element.container.stacking.MutableRatioContainer;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImmutableRatioContainer extends ImmutableContainer {

//    protected final List<ImmutableElement> children = new ArrayList<>();
    protected final Map<ImmutableElement, Integer> childRatioMap = new HashMap<>();
    protected final Axis majorAxis;
    protected final Vector2i fixedSize;

    protected ImmutableRatioContainer(Builder builder) {
        super(builder);
        majorAxis = builder.majorAxis == null ? Axis.VERTICAL : builder.majorAxis;
        fixedSize = Objects.requireNonNull(builder.fixedSize);
    }

    public Map<ImmutableElement, Integer> getChildRatioMap() {
        return childRatioMap;
    }

    /**
     * @return An immutable copy of the children contained in this {@link ImmutableRatioContainer}.
     */
    @Override
    public List<ImmutableElement> getChildren() {
        return List.copyOf(childRatioMap.keySet());
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            MutableRatioContainer container = new MutableRatioContainer(parent, margin, tag, zPosition, majorAxis, fixedSize);
            Map<MutableElement, Integer> mutableRatioMap = childRatioMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().asMutable(container),
                    Map.Entry::getValue
                ));

            container.getChildRatioMap().putAll(mutableRatioMap);
            // Update positions and size
            container.propagateUpdate();

            listeners.forEach(container::subscribe);
            onCreate.forEach(consumer -> consumer.accept(container));

            return container;
        };
    }

    public static class Builder extends ImmutableContainer.Builder<ImmutableRatioContainer, Builder, MutableRatioContainer> {

        protected final Map<Function<ImmutableRatioContainer, ImmutableElement>, Integer> childFunctionRatioMap = new HashMap<>();
        protected Axis majorAxis;
        protected Vector2i fixedSize;

        public Builder add(ImmutableElement element, int ratio) {
            childFunctionRatioMap.put(page -> element, ratio);
            return self();
        }

        public Builder add(Function<ImmutableRatioContainer, ImmutableElement> function,
                                                      int ratio) {
            childFunctionRatioMap.put(function, ratio);
            return self();
        }

        public Builder setMajorAxis(Axis majorAxis) {
            this.majorAxis = majorAxis;
            return self();
        }

        public Builder setFixedSize(Vector2i fixedSize) {
            this.fixedSize = fixedSize;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public ImmutableRatioContainer build() {
            ImmutableRatioContainer container = new ImmutableRatioContainer(this);
            container.getChildRatioMap().putAll(childFunctionRatioMap.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().apply(container),
                    Map.Entry::getValue
                )));
            return container;
        }

    }

}
