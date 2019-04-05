package me.dylancurzon.pages.element.container.stacking;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MutableRatioContainer extends MutableContainer {

    private final Map<MutableElement, Integer> childRatioMap = new HashMap<>();

    // By default, stack elements from top to bottom
    private Axis majorAxis = Axis.VERTICAL;
    private Vector2i fixedSize;

    public MutableRatioContainer(@Nullable MutableContainer parent,
                                 Spacing margin,
                                 @Nullable String tag,
                                 @Nullable Integer zPosition,
                                 @Nullable Axis majorAxis,
                                 Vector2i fixedSize) {
        super(parent, margin, tag, zPosition);
        if (majorAxis != null) this.majorAxis = majorAxis;
        Objects.requireNonNull(fixedSize);
        this.fixedSize = fixedSize;
        // These values will be ignored, but it's better to set them anyway
        minimumSize = fixedSize;
        maximumSize = fixedSize;
    }

    public Map<MutableElement, Integer> getChildRatioMap() {
        return childRatioMap;
    }

    @Override
    public Vector2i getSize() {
        // It doesn't matter where Elements are in this Container, the size is inherently fixed
        return fixedSize;
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        Map<MutableElement, Vector2i> positions = new HashMap<>();

        int totalRatio = childRatioMap.values().stream()
            .mapToInt(i -> i)
            .sum();

        Vector2i currentPosition = Vector2i.of(0, 0);
        for (Map.Entry<MutableElement, Integer> childEntry : childRatioMap.entrySet()) {
            MutableElement childElement = childEntry.getKey();
            positions.put(childElement, currentPosition);

            int childRatio = childEntry.getValue();
            double relativeRatio = ((double) childRatio) / totalRatio;
            Vector2i positionOffset = fixedSize.toDouble().mul(relativeRatio).toInt();

            currentPosition = currentPosition.add(
                majorAxis == Axis.HORIZONTAL
                    ? positionOffset.setY(0)
                    : positionOffset.setX(0)
            );
        }

        return positions;
    }

    /**
     * @return An immutable copy of the children contained by this {@link MutableRatioContainer}.
     */
    @Override
    public List<MutableElement> getChildren() {
        return List.copyOf(childRatioMap.keySet());
    }

}
