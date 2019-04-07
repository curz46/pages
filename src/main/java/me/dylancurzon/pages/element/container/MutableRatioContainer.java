package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MutableRatioContainer extends MutableContainer {

    private final Map<MutableElement, Integer> childRatioMap = new LinkedHashMap<>();

    // By default, stack elements from top to bottom
    private Axis majorAxis = Axis.VERTICAL;
    private boolean centering;

    public MutableRatioContainer(@Nullable MutableContainer parent,
                                 Spacing margin,
                                 @Nullable String tag,
                                 @Nullable Integer zPosition,
                                 @Nullable Axis majorAxis,
                                 boolean centering,
                                 Vector2i fixedSize,
                                 @Nullable Vector2i minimumSize,
                                 @Nullable Vector2i maximumSize,
                                 ElementDecoration decoration) {
        super(parent, margin, tag, zPosition, fixedSize, minimumSize, maximumSize, decoration);

        if (fixedSize == null) {
            throw new IllegalArgumentException("fixedSize not defined on a RatioContainer: " + this);
        }

        if (majorAxis != null) this.majorAxis = majorAxis;
        this.centering = centering;
    }

    public Map<MutableElement, Integer> getChildRatioMap() {
        return childRatioMap;
    }

    @Override
    public Vector2i getSize() {
        // It doesn't matter where Elements are in this Container, the size is inherently fixed
        return Objects.requireNonNull(fixedSize);
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
            if (centering) {
                if (fixedSize == null) {
                    throw new IllegalStateException(
                        "MutableStackingContainer is set as centering without a fixedSize: " + this);
                }

                System.out.println("CENTERING: " + childElement);
                System.out.println("fixedSize: " + fixedSize);
                System.out.println("parentSize: " + getSize());
                System.out.println("childSize: " + childElement.getMarginedSize());

                Vector2i centeringOffset = getSize().div(2) // move to middle
                    .sub(childElement.getMarginedSize().div(2)) // adjust for element width, allow movement with margin
                    .toInt();

                System.out.println("currentPosition: " + currentPosition);
                System.out.println("centeringOffset: " + centeringOffset);

                positions.put(
                    childElement,
                    currentPosition.add(
                        majorAxis == Axis.HORIZONTAL
                            ? centeringOffset.setX(0) // minorAxis=VERTICAL
                            : centeringOffset.setY(0) // minorAxis=HORIZONTAL
                    )
                );

                System.out.println("finalPosition: " + currentPosition.add(
                    majorAxis == Axis.HORIZONTAL
                        ? centeringOffset.setX(0) // minorAxis=VERTICAL
                        : centeringOffset.setY(0) // minorAxis=HORIZONTAL
                ));
            } else {
                positions.put(childElement, currentPosition);
            }

            int childRatio = childEntry.getValue();
            double relativeRatio = ((double) childRatio) / totalRatio;
            Vector2i positionOffset = fixedSize.toDouble().mul(relativeRatio).toInt();

            currentPosition = currentPosition.add(
                majorAxis == Axis.HORIZONTAL
                    ? positionOffset.setY(0)
                    : positionOffset.setX(0)
            );

            // The allocated size for a ratio container is the offset on the major axis and the fixedSize on the minor
            // axis
            Vector2i allocatedSize = majorAxis == Axis.HORIZONTAL
                ? positionOffset.setY(fixedSize.getY())
                : positionOffset.setX(fixedSize.getX());
            // Set allocated size
            childElement.setAllocatedSize(allocatedSize);
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
