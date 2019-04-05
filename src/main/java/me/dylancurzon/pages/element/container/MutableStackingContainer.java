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

    public MutableStackingContainer(@Nullable MutableContainer parent,
                                    Spacing margin,
                                    @Nullable String tag,
                                    @Nullable Integer zPosition,
                                    @Nullable Axis majorAxis,
                                    @Nullable Vector2i fixedSize,
                                    @Nullable Vector2i minimumSize,
                                    @Nullable Vector2i maximumSize,
                                    ElementDecoration decoration) {
        super(parent, margin, tag, zPosition, fixedSize, minimumSize, maximumSize, decoration);
        if (majorAxis != null) this.majorAxis = majorAxis;
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        // In order to stack Elements, we need to know which direction to do it in
        // This requires a new property: majorAxis
        Map<MutableElement, Vector2i> positions = new HashMap<>();

        // Stacking is just a case of cumulatively putting Elements on top of each other while respecting Margins
        Vector2i currentPosition = Vector2i.of(0, 0);
        for (MutableElement childElement : children) {
            positions.put(childElement, currentPosition);

            Vector2i childSize = childElement.getSize();
            Spacing childMargin = childElement.getMargin();

            Vector2i elementBoundOffset = Vector2i.of(
                childMargin.getLeft() + childSize.getX() + childMargin.getRight(),
                childMargin.getBottom() + childSize.getY() + childMargin.getTop()
            );

            // Only affect the majorAxis
            currentPosition = majorAxis == Axis.HORIZONTAL
                ? currentPosition.add(elementBoundOffset.setY(0))
                : currentPosition.add(elementBoundOffset.setX(0));
        }

        return positions;
    }

    @Override
    public List<MutableElement> getChildren() {
        return children;
    }

}
