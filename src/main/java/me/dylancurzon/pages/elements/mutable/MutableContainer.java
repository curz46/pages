package me.dylancurzon.pages.elements.mutable;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.*;
import me.dylancurzon.pages.util.Cached;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.pages.animation.Animation;
import me.dylancurzon.pages.animation.QuarticEaseInAnimation;
import me.dylancurzon.pages.elements.container.ImmutableContainer;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.dylancurzon.pages.elements.container.Positioning.*;

public abstract class MutableContainer extends MutableElement {

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

    protected MutableContainer(Spacing margin, ImmutableContainer container,
                               List<MutableElement> elements) {
        super(margin, container.getInteractOptions());
        this.container = container;
        this.elements = elements;

        fillColor = container.getFillColor().orElse(null);
        lineColor = container.getLineColor().orElse(null);
        lineWidth = container.getLineWidth().orElse(null);
    }

    public abstract List<AlignedElement> draw();

    @NotNull
    public List<MutableElement> getElements() {
        return elements;
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
    public void click(@NotNull Vector2i position) {
        super.click(position);
        // for each MutableElement of this container, find the position relative to its position in this
        // container.
        Map<MutableElement, Vector2i> elementPositionMap = getPositions();
        elementPositionMap.forEach((mut, elementPos) -> {
            Vector2i relative = position.sub(elementPos);
            Vector2i size = mut.getSize();
            // ensure in bounds
            if (relative.getX() < 0 || relative.getX() >= size.getX() ||
                relative.getY() < 0 || relative.getY() >= size.getY()) {
                return;
            }
            mut.click(relative);
        });
    }

    @Override
    public Vector2i getMousePosition(MutableElement element) {
        Vector2i position = getPositions().get(element);
        if (position == null || parent == null) return null;
        Vector2i mousePosition = parent.getMousePosition(this);
        if (mousePosition == null) return null;
        return mousePosition.sub(position);
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
