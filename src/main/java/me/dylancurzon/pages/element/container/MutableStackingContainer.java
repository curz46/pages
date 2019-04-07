package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutableStackingContainer extends MutableContainer {

    private final List<MutableElement> children = new ArrayList<>();

    // By default, stack elements from top to bottom
    private Axis majorAxis = Axis.VERTICAL;
    // If a StackingContainer is centering, it will center children on the *minor* axis
    // Note: this *requires* a fixedSize
    private boolean centering;

    public MutableStackingContainer(@Nullable MutableContainer parent,
                                    Spacing margin,
                                    @Nullable String tag,
                                    @Nullable Integer zPosition,
                                    @Nullable Axis majorAxis,
                                    boolean centering,
                                    @Nullable Vector2i fixedSize,
                                    @Nullable Vector2i minimumSize,
                                    @Nullable Vector2i maximumSize,
                                    ElementDecoration decoration) {
        super(parent, margin, tag, zPosition, fixedSize, minimumSize, maximumSize, decoration);
        if (majorAxis != null) this.majorAxis = majorAxis;
        this.centering = centering;
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        // In order to stack Elements, we need to know which direction to do it in
        // This requires a new property: majorAxis
        Map<MutableElement, Vector2i> positions = new HashMap<>();

        // Stacking is just a case of cumulatively putting Elements on top of each other while respecting Margins
        Vector2i currentPosition = Vector2i.of(0, 0);
        for (MutableElement childElement : children) {
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

                positions.put(
                    childElement,
                    currentPosition.add(
                        majorAxis == Axis.HORIZONTAL
                            ? centeringOffset.setX(0) // minorAxis=VERTICAL
                            : centeringOffset.setY(0) // minorAxis=HORIZONTAL
                    )
                );
            } else {
                positions.put(childElement, currentPosition);
            }

            Vector2i childMarginedSize = childElement.getMarginedSize();

            // Only affect the majorAxis
            currentPosition = majorAxis == Axis.HORIZONTAL
                ? currentPosition.add(childMarginedSize.setY(0))
                : currentPosition.add(childMarginedSize.setX(0));

            // Set allocated size for element
            childElement.setAllocatedSize(childElement.getMarginedSize());
        }

        return positions;
    }

    @Override
    public List<MutableElement> getChildren() {
        return children;
    }

}
