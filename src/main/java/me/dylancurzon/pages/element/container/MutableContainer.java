package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.List;
import java.util.*;

import static me.dylancurzon.pages.element.container.Positioning.*;

public class MutableContainer extends MutableElement {

    private final ImmutableContainer container;
    private final List<MutableElement> children;

    private Vector2i size;
    private Map<MutableElement, Vector2i> positions;

    private Color fillColor;
    private Color lineColor;
    private Integer lineWidth;

    public MutableContainer(Spacing margin, ImmutableContainer container, List<MutableElement> children) {
        super(margin);
        this.container = container;
        this.children = children;

        fillColor = container.getFillColor().orElse(null);
        lineColor = container.getLineColor().orElse(null);
        lineWidth = container.getLineWidth().orElse(null);

        positions = computePositions();
    }

    /**
     * Recalculates the positions of this {@link MutableContainer}'s children and its size, then propagates this update
     * to the parent.
     */
    @Override
    public void propagateUpdate() {
        positions = computePositions();
        size = computeSize();

        super.propagateUpdate();
    }

    public Map<MutableElement, Vector2i> flatten() {
        Map<MutableElement, Vector2i> elements = new HashMap<>();

        positions.forEach((element, position) -> {
            if (element instanceof MutableContainer) {
                Map<MutableElement, Vector2i> containerElements = ((MutableContainer) element).flatten();
                containerElements.forEach((containerElement, containerPosition) ->
                    elements.put(containerElement, position.add(containerPosition)));
            }

            elements.put(element, position);
        });

        return elements;
    }

    public Vector2i computeSize() {
        // TODO: Properties accessed here should be replicated in this Object, not referenced.
        // This allows users to mutate the properties if desired
        Vector2i size = container.getSize();
        if (size == null || size.getX() == -1 || size.getY() == -1) {
            Vector2i calculatedSize = Vector2i.of(0, 0);
            for (MutableElement mut : children) {
                Vector2i elementSize = mut.getMarginedSize();
                calculatedSize = calculatedSize.add(
                    container.getPositioning() == Positioning.INLINE
                        ? Vector2i.of(elementSize.getX(), 0)
                        : Vector2i.of(0, elementSize.getY())
                );
                if (container.getPositioning() != Positioning.INLINE
                    && calculatedSize.getX() < elementSize.getX()) {
                    calculatedSize = calculatedSize.setX(elementSize.getX());
                }
                if (container.getPositioning() == Positioning.INLINE
                    && calculatedSize.getY() < elementSize.getY()) {
                    calculatedSize = calculatedSize.setY(elementSize.getY());
                }
            }

            if (size == null) {
                return calculatedSize;
            }
            if (size.getX() == -1) {
                size = size.setX(calculatedSize.getX());
            }
            if (size.getY() == -1) {
                size = size.setY(calculatedSize.getY());
            }
        }

        return size;
    }

    /**
     * @return A map of each MutableElement (in {@link this#children} and its calculated position. It factors in
     * if the {@link this#container} is centering, inline, padded and includes each MutableElement's margin.
     */
    private Map<MutableElement, Vector2i> computePositions() {
        Map<MutableElement, Vector2i> positions = new LinkedHashMap<>();
        if (children.isEmpty()) return positions;

        if (container.isCentering()) {
            for (MutableElement mut : children) {
                Vector2i elementSize = mut.getSize();

                // find centered position based on this container's size
                Vector2i centered = container.getSize()
                    .div(2)
                    .sub(elementSize.div(2))
                    .floor().toInt();
                positions.put(mut, centered);
            }
        } else {
            Spacing padding = container.getPadding();
            Vector2i pos = Vector2i.of(
                    padding.getLeft(),
                    padding.getTop()
            );
            for (MutableElement mut : children) {
                Vector2i delta =
                    Vector2i.of(mut.getMargin().getLeft(), mut.getMargin().getTop());
                if (container.getPositioning() != OVERLAY) {
                    pos = pos.add(
                        Vector2i.of(mut.getMargin().getLeft(), mut.getMargin().getTop())
                    );
                } else {
                    pos = delta;
                }
                Vector2i elementSize = mut.getSize();

                positions.put(mut, pos);

                if (container.getPositioning() == INLINE) {
                    pos = pos.add(Vector2i.of(mut.getMargin().getRight() + elementSize.getX(), 0));
                } else if (container.getPositioning() == DEFAULT){
                    pos = pos.add(Vector2i.of(0, mut.getMargin().getBottom() + elementSize.getY()));
                }
            }
        }

        return positions;
    }

    public Optional<Color> getFillColor() {
        return Optional.ofNullable(fillColor);
    }

    public Optional<Color> getLineColor() {
        return Optional.ofNullable(lineColor);
    }

    public Optional<Integer> getLineWidth() {
        return Optional.ofNullable(lineWidth);
    }

    public List<MutableElement> getChildren() {
        return children;
    }

    public Map<MutableElement, Vector2i> getPositions() {
        return positions;
    }

    @Override
    public Vector2i getSize() {
        if (size == null) {
            size = computeSize();
        }
        return size;
    }

}
