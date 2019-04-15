package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MutableAbsoluteContainer extends MutableContainer {

    public MutableAbsoluteContainer(@Nullable MutableContainer parent,
                                    Spacing margin,
                                    @Nullable String tag,
                                    @Nullable Integer zIndex,
                                    boolean visible,
                                    @Nullable Vector2i fixedSize,
                                    @Nullable Vector2i minimumSize,
                                    @Nullable Vector2i maximumSize,
                                    Axis majorAxis,
                                    ElementDecoration decoration) {
        super(parent, margin, tag, zIndex, visible, fixedSize, minimumSize, maximumSize, majorAxis, decoration);
        positions = Map.of();
    }

    public void setPositions(Map<MutableElement, Vector2i> positions) {
        this.positions = Objects.requireNonNull(positions);
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        // Set the allocated size for all elements
        for (MutableElement element : positions.keySet()) {
            element.setAllocatedSize(element.getMarginedSize());
        }

        // Positions in an AbsoluteMutableContainer are not computed, they are provided.
        // However, offset should still be respected
        return positions.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().add(Vector2i.of(offsetX, offsetY)),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new
            ));
    }

    public Map<MutableElement, Vector2i> getPositions() {
        return positions;
    }

    /**
     * @return An immutable copy of the children contained by this {@link MutableAbsoluteContainer}. Modifications to
     * a container of this type must be done through {@link this#setPositions(Map)}}.
     */
    @Override
    public List<MutableElement> getChildren() {
        return List.copyOf(positions.keySet());
    }

}
