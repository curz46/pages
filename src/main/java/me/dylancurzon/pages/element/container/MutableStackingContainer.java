package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MutableStackingContainer extends MutableContainer {

    private final List<MutableElement> children = new ArrayList<>();

    // By default, stack elements from top to bottom
    private Axis majorAxis = Axis.VERTICAL;
    // If a StackingContainer is centering, it will center children on the *minor* axis
    // Note: this *requires* a fixedSize
    private boolean centerOnX;
    private boolean centerOnY;

    public MutableStackingContainer(@Nullable MutableContainer parent,
                                    Spacing margin,
                                    @Nullable String tag,
                                    @Nullable Integer zPosition,
                                    @Nullable Axis majorAxis,
                                    boolean centerOnX,
                                    boolean centerOnY,
                                    @Nullable Vector2i fixedSize,
                                    @Nullable Vector2i minimumSize,
                                    @Nullable Vector2i maximumSize,
                                    ElementDecoration decoration) {
        super(parent, margin, tag, zPosition, fixedSize, minimumSize, maximumSize, decoration);
        if (majorAxis != null) this.majorAxis = majorAxis;
        this.centerOnX = centerOnX;
        this.centerOnY = centerOnY;
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        // In order to stack Elements, we need to know which direction to do it in
        // This requires a new property: majorAxis
        Map<MutableElement, Vector2i> positions = new HashMap<>();

        // Stacking is just a case of cumulatively putting Elements on top of each other while respecting Margins
        Vector2i currentPosition = Vector2i.of(0, 0);

        boolean centerOnMajor = centerOnX && majorAxis == Axis.HORIZONTAL || centerOnY && majorAxis == Axis.VERTICAL;
        boolean centerOnMinor = centerOnX && majorAxis == Axis.VERTICAL   || centerOnY && majorAxis == Axis.HORIZONTAL;

        MutableElement firstElement = null;
        MutableElement lastElement = null;

        for (MutableElement childElement : children) {
            // currentPosition with this element's margin added such that it will be properly respected
            Vector2i marginedPosition = currentPosition.add(Vector2i.of(
                childElement.getMargin().getLeft(),
                childElement.getMargin().getTop()
            ));

            if (centerOnMinor) {
                if (fixedSize == null) {
                    throw new IllegalStateException(
                        "MutableStackingContainer is set as centering without a fixedSize: " + this);
                }

//                System.out.println("CENTERING: " + childElement);
//                System.out.println("fixedSize: " + fixedSize);
//                System.out.println("parentSize: " + getSize());
//                System.out.println("childSize: " + childElement.getMarginedSize());

                Vector2i centeringOffset = getSize().div(2) // move to middle
                    .sub(childElement.getMarginedSize().div(2)) // adjust for element width, allow movement with margin
                    .toInt();

                positions.put(
                    childElement,
                    marginedPosition.add(
                        majorAxis == Axis.HORIZONTAL
                            ? centeringOffset.setX(0) // minorAxis=VERTICAL
                            : centeringOffset.setY(0) // minorAxis=HORIZONTAL
                    )
                );
            } else {
                positions.put(childElement, marginedPosition);
            }

            Vector2i childMarginedSize = childElement.getMarginedSize();

            // Only affect the majorAxis
            currentPosition = majorAxis == Axis.HORIZONTAL
                ? currentPosition.add(childMarginedSize.setY(0))
                : currentPosition.add(childMarginedSize.setX(0));

            // Set allocated size for element
            childElement.setAllocatedSize(childElement.getMarginedSize());

            if (firstElement == null) firstElement = childElement;
            lastElement = childElement;
        }

        // In order to account for centerOnMajor, we need to transform all of these positions such that the midpoint
        // of the two outer elements is the middle of this container.
        if (centerOnMajor && !positions.isEmpty()) {
            // If the size of positions is 1, then firstElement and lastElement will be equal. However, the resultant
            // midpoint can be resolved in the same way.
            Vector2i midpointChildren = positions.get(firstElement)
                .add(positions.get(lastElement).add(lastElement.getMarginedSize()))
                .div(2)
                .toInt();
            Vector2i midpointContainer = getSize().div(2).toInt();
            // Now we can calculate the difference of this midpoint to the midpoint of the container
            Vector2i midpointOffset = midpointContainer.sub(midpointChildren);
            // With this, we can transform all positions to be correct
            positions = positions.entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().add(
                        // Only affect the majorAxis
                        majorAxis == Axis.HORIZONTAL
                            ? midpointOffset.setY(0)
                            : midpointOffset.setX(0)
                    )
                ));
        }

        return positions;
    }

    @Override
    public List<MutableElement> getChildren() {
        return children;
    }

}
