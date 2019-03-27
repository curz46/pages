package me.dylancurzon.pages.elements.mutable;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.AlignedElement;
import me.dylancurzon.pages.animation.Animation;
import me.dylancurzon.pages.animation.QuarticEaseInAnimation;
import me.dylancurzon.pages.elements.container.ImmutableContainer;
import me.dylancurzon.pages.elements.container.Positioning;
import me.dylancurzon.pages.util.Cached;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static me.dylancurzon.pages.elements.container.Positioning.*;

public class MutableContainer extends MutableElement {

    private static final double SCROLL_FACTOR = 8;

    private final ImmutableContainer container;

    private final List<MutableElement> elements;
    private final Cached<Map<MutableElement, Vector2i>> positions = new Cached<>();

    protected double scroll;
    protected double scrollVelocity;

    private TransformHandler transform;

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

    public List<AlignedElement> draw() {
        List<AlignedElement> elements = new ArrayList<>();
        for (Map.Entry<MutableElement, Vector2i> entry : getPositions().entrySet()) {
            MutableElement element = entry.getKey();
            Vector2i position = entry.getValue();

            if (element instanceof MutableContainer) {
                elements.addAll(
                    ((MutableContainer) element).draw().stream()
                        .map(containedElement -> new AlignedElement(
                            containedElement.getElement(),
                            position.add(containedElement.getPosition())
                        ))
                        .collect(Collectors.toList())
                );
            }
            elements.add(new AlignedElement(element, position));
        }

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
                positions.put(mut, centered.sub(Vector2i.of(0, (int) scroll)));
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

                positions.put(mut, pos.sub(Vector2i.of(0, (int) scroll)));

                if (container.getPositioning() == INLINE) {
                    pos = pos.add(Vector2i.of(mut.getMargin().getRight() + elementSize.getX(), 0));
                } else if (container.getPositioning() == DEFAULT){
                    pos = pos.add(Vector2i.of(0, mut.getMargin().getBottom() + elementSize.getY()));
                }
            }
        }

        return positions;
    }

    @Override
    public void tick() {
        if (!container.isScrollable()) return;
        double originalScroll = scroll;
        scroll += scrollVelocity;
        scrollVelocity *= 0.8;
        checkBounds();
        if (originalScroll != scroll) {
            positions.clear();
        }
    }

    public void scroll(double amount) {
        if (!container.isScrollable()) return;
        if (transform != null) {
            transform = null;
        }
        scrollVelocity += amount * SCROLL_FACTOR;
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

    private void checkBounds() {
        if ((scroll + 5) < 0) {
            transform = new TransformHandler(
                Vector2d.of(0, scroll),
                Vector2d.of(0, 0),
                new QuarticEaseInAnimation(0, 1, 20)
            );
        }
        double max = getMaxScroll();
        if ((scroll - 5) > max) {
            transform = new TransformHandler(
                Vector2d.of(0, scroll),
                Vector2d.of(0, max),
                new QuarticEaseInAnimation(0, 1, 20)
            );
        }

        if (transform != null) {
            transform.tick();
            scroll = transform.getPosition().getY();
        }
    }

    private double getMaxScroll() {
        Vector2i size = Vector2i.of(
            0,
            0
        );
        for (MutableElement mut : elements) {
            Vector2i elementSize = mut.getSize()
                .add(Vector2i.of(
                    mut.getMargin().getLeft() + mut.getMargin().getRight(),
                    mut.getMargin().getBottom() + mut.getMargin().getTop()
                ));
            size = size.add(Vector2i.of(0, elementSize.getY()));
        }
        return size.getY() - container.getPaddedSize().getY();
    }

    public static class TransformHandler {

        private final Vector2d initialPosition;
        private final Vector2d destination;
        private final Animation animation;

        public TransformHandler(Vector2d initialPosition, Vector2d destination,
                                Animation animation) {
            this.initialPosition = initialPosition;
            this.destination = destination;
            this.animation = animation;
        }

        public void tick() {
            animation.tick();
        }

        public Vector2d getPosition() {
            double progress = animation.determineValue();
            Vector2d delta = destination.sub(initialPosition).mul(progress);
            return initialPosition.add(delta);
        }

        public boolean isCompleted() {
            return animation.isCompleted();
        }

    }

}
