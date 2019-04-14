package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Vector2i;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ImmutableAbsoluteContainer extends ImmutableContainer {

    protected final Map<ImmutableElement, Vector2i> positions = new LinkedHashMap<>();

    protected ImmutableAbsoluteContainer(Builder builder) {
        super(builder);
    }

    public Map<ImmutableElement, Vector2i> getPositions() {
        return positions;
    }

    /**
     * @return An immutable copy of the children contained by this {@link ImmutableAbsoluteContainer}.
     */
    @Override
    public List<ImmutableElement> getChildren() {
        return List.copyOf(positions.keySet());
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            MutableAbsoluteContainer container = new MutableAbsoluteContainer(
                parent,
                margin,
                tag,
                zIndex,
                visible,
                fixedSize,
                minimumSize,
                maximumSize,
                decoration
            );
            Map<MutableElement, Vector2i> mutablePositions = new LinkedHashMap<>();
            positions.forEach(((immutableElement, position) ->
                mutablePositions.put(immutableElement.asMutable(container), position)));
            container.setPositions(mutablePositions);
            // Update positions and size
            container.propagateUpdate();

            listeners.forEach(container::subscribe);
            onCreate.forEach(consumer -> consumer.accept(container));

            return container;
        };
    }

    public static class Builder extends ImmutableContainer.Builder<ImmutableAbsoluteContainer, Builder, MutableAbsoluteContainer> {

        protected final Map<Function<ImmutableAbsoluteContainer, ImmutableElement>, Vector2i> childrenFunctions = new LinkedHashMap<>();

        public Builder add(ImmutableElement element, Vector2i position) {
            childrenFunctions.put(page -> element, position);
            return self();
        }

        public Builder add(Function<ImmutableAbsoluteContainer, ImmutableElement> function, Vector2i position) {
            childrenFunctions.put(function, position);
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public ImmutableAbsoluteContainer build() {
            ImmutableAbsoluteContainer container = new ImmutableAbsoluteContainer(this);
            childrenFunctions
                .forEach((function, position) -> container.getPositions().put(function.apply(container), position));

            return container;
        }

    }

}
