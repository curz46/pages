package me.dylancurzon.pages.elements.mutable;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.elements.container.ImmutableContainer;
import me.dylancurzon.pages.elements.container.Positioning;
import me.dylancurzon.pages.util.Cached;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.List;
import java.util.*;

import static me.dylancurzon.pages.elements.container.Positioning.*;

public class MutableContainer extends MutableElement {

    private final ImmutableContainer container;

    private final List<MutableElement> elements;
    private final Cached<Map<MutableElement, Vector2i>> positions = new Cached<>();

    private Color fillColor;
    private Color lineColor;
    private Integer lineWidth;

    public MutableContainer(Spacing margin, ImmutableContainer container, List<MutableElement> elements) {
        super(margin);
        this.container = container;
        this.elements = elements;

        fillColor = container.getFillColor().orElse(null);
        lineColor = container.getLineColor().orElse(null);
        lineWidth = container.getLineWidth().orElse(null);
    }

    @NotNull
    public List<MutableElement> getElements() {
        return elements;
    }

    public Map<MutableElement, Vector2i> flatten() {
        Map<MutableElement, Vector2i> elements = new HashMap<>();

        getPositions().forEach((element, position) -> {
            if (element instanceof MutableContainer) {
                Map<MutableElement, Vector2i> containerElements = ((MutableContainer) element).flatten();
                containerElements.forEach((containerElement, containerPosition) -> {
                    elements.put(containerElement, position.add(containerPosition));
                });
            }

            elements.put(element, position);
        });

        return elements;
    }

    @Override
    public Vector2i calculateSize() {
        // TODO: Properties accessed here should be replicated in this Object, not referenced.
        // This allows users to mutate the properties if desired
        Vector2i size = container.getSize();
        if (size == null || size.getX() == -1 || size.getY() == -1) {
            Vector2i calculatedSize = Vector2i.of(0, 0);
            for (MutableElement mut : elements) {
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


    public Map<MutableElement, Vector2i> getPositions() {
        Map<MutableElement, Vector2i> result = positions.get()
            .orElseGet(() -> {
                Map<MutableElement, Vector2i> positions = calculatePositions();
                this.positions.set(positions);
                return positions;
            });

        return result;
    }

    /**
     * @return A map of each MutableElement (in {@link this#elements} and its calculated position. It factors in
     * if the {@link this#container} is centering, inline, padded and includes each MutableElement's margin.
     */
    private Map<MutableElement, Vector2i> calculatePositions() {
        Map<MutableElement, Vector2i> positions = new LinkedHashMap<>();
        if (elements.isEmpty()) return positions;

        if (container.isCentering()) {
            for (MutableElement mut : elements) {
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
            for (MutableElement mut : elements) {
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

}
