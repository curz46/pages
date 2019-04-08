package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Vector2i;

import java.sql.BatchUpdateException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImmutableRatioContainer extends ImmutableContainer {

//    protected final List<ImmutableElement> children = new ArrayList<>();
    protected final Map<ImmutableElement, Integer> childRatioMap = new LinkedHashMap<>();
    protected final Axis majorAxis;
    protected final boolean centerOnX;
    protected final boolean centerOnY;

    protected ImmutableRatioContainer(Builder builder) {
        super(builder);
        majorAxis = builder.majorAxis == null ? Axis.VERTICAL : builder.majorAxis;
        centerOnX = builder.centerOnX;
        centerOnY = builder.centerOnY;
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
            MutableRatioContainer container = new MutableRatioContainer(
                parent,
                margin,
                tag,
                zPosition,
                majorAxis,
                centerOnX,
                centerOnY,
                fixedSize,
                minimumSize,
                maximumSize,
                decoration
            );
//            Map<MutableElement, Integer> mutableRatioMap = childRatioMap
//                .entrySet().stream()
//                .collect(Collectors.toMap(
//                    entry -> entry.getKey().asMutable(container),
//                    Map.Entry::getValue,
//                    (u, v) -> {
//                        throw new IllegalStateException(String.format("Duplicate key %s", u));
//                    },
//                    LinkedHashMap::new
//                ));

            childRatioMap.forEach(((immutableElement, ratio) ->
                container.getChildRatioMap().put(immutableElement.asMutable(container), ratio)));

//            container.getChildRatioMap().putAll(mutableRatioMap);
            // Update positions and size
            container.propagateUpdate();

            listeners.forEach(container::subscribe);
            onCreate.forEach(consumer -> consumer.accept(container));

            return container;
        };
    }

    public static class Builder extends ImmutableContainer.Builder<ImmutableRatioContainer, Builder, MutableRatioContainer> {

        protected final Map<Function<ImmutableRatioContainer, ImmutableElement>, Integer> childFunctionRatioMap = new LinkedHashMap<>();
        protected Axis majorAxis;
        protected boolean centerOnX;
        protected boolean centerOnY;

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
        public ImmutableRatioContainer build() {
            // Allow a null fixedSize such that it can be defined during execution
            if (fixedSize == null) {
                fixedSize = Vector2i.of(0, 0);
            }

            ImmutableRatioContainer container = new ImmutableRatioContainer(this);
            childFunctionRatioMap.forEach(((function, ratio) ->
                container.getChildRatioMap().put(function.apply(container), ratio)));
            return container;
        }

    }

}
