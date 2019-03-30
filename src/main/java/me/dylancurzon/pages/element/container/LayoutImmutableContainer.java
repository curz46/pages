package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.dylancurzon.pages.element.container.Positioning.INLINE;

public class LayoutImmutableContainer extends ImmutableElement implements ImmutableContainer {

    private final List<Pair<Integer, Function<ImmutableContainer, ImmutableElement>>> elements;
    private final Vector2i size;
    private final Spacing padding;
    private final Positioning positioning;
    private final boolean centering;
    private final boolean scrollable;
    protected final Color fillColor;
    private final Color lineColor;
    private final Integer lineWidth;

    private LayoutImmutableContainer(Builder builder) {
        super(builder);
        elements = builder.elements;
        size = builder.size;
        positioning = builder.positioning;
        centering = builder.centering;
        if (builder.padding == null) {
            padding = Spacing.ZERO;
        } else {
            padding = builder.padding;
        }
        scrollable = builder.scrollable;
        fillColor = builder.fillColor;
        lineColor = builder.lineColor;
        lineWidth = builder.lineWidth;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            int total = elements.stream()
                .map(Pair::getKey).mapToInt(Integer::intValue).sum();
            List<ImmutableElement> wrappedElements = elements.stream()
                .map(pair -> ImmutableContainer.builder()
                    .setCentering(centering)
                    .setSize((positioning == INLINE
                        ? size.toDouble().mul(Vector2d.of(((double) pair.getKey()) / total, 1))
                        : size.toDouble().mul(Vector2d.of(1, ((double) pair.getKey()) / total)))
                        .ceil().toInt())
                    .add(pair.getValue())
                    .build())
                .collect(Collectors.toList());
            MutableContainer container = (MutableContainer) ImmutableContainer.builder()
                .setSize(size)
                .setPadding(padding)
                .setPositioning(positioning)
                .setScrollable(scrollable)
                .setFillColor(fillColor)
                .setLineColor(lineColor)
                .setLineWidth(lineWidth)
                .add(wrappedElements)
                .build()
                .asMutable(parent);
            listeners.forEach(container::subscribe);
            onCreate.forEach(consumer -> consumer.accept(container));
            return container;
        };
    }

    @Override
    public Spacing getPadding() {
        return padding;
    }

    @Override
    public Vector2i getSize() {
        return size;
    }

    @Override
    public Vector2i getMarginedSize() {
        return getSize().add(
            Vector2i.of(
                margin.getLeft() + margin.getRight(),
                margin.getBottom() + margin.getTop()
            )
        );
    }

    @Override
    public Vector2i getPaddedSize() {
        return getSize().sub(
            Vector2i.of(
                padding.getLeft() + padding.getRight(),
                padding.getBottom() + padding.getTop()
            )
        );
    }

    @Override
    public boolean isCentering() {
        return centering;
    }

    @Override
    public Positioning getPositioning() {
        return positioning;
    }

    @Override
    public Optional<Color> getFillColor() {
        return Optional.ofNullable(fillColor);
    }

    @Override
    public Optional<Color> getLineColor() {
        return Optional.ofNullable(lineColor);
    }

    @Override
    public Optional<Integer> getLineWidth() {
        return Optional.ofNullable(lineWidth);
    }

    public static class Builder extends ImmutableElement.Builder<LayoutImmutableContainer, Builder, MutableContainer> {

        private final List<Pair<Integer, Function<ImmutableContainer, ImmutableElement>>> elements = new ArrayList<>();
        private Vector2i size;
        private Spacing padding;
        private Positioning positioning;
        private boolean centering;
        private boolean scrollable;
        private Color fillColor;
        private Color lineColor;
        private Integer lineWidth;

        public Builder add(int ratio, ImmutableElement element) {
            elements.add(Pair.of(ratio, page -> element));
            return this;
        }

        public Builder add(int ratio,
                           Function<ImmutableContainer, ImmutableElement> fn) {
            elements.add(Pair.of(ratio, fn));
            return this;
        }

        public Builder setSize(Vector2i size) {
            this.size = size;
            return this;
        }

        public Builder setPadding(Spacing padding) {
            this.padding = padding;
            return this;
        }

        public Builder setPositioning(Positioning positioning) {
            this.positioning = positioning;
            return this;
        }

        public Builder setCentering(boolean centering) {
            this.centering = centering;
            return this;
        }

        public Builder setFillColor(Color color) {
            fillColor = color;
            return self();
        }

        public Builder setLineColor(Color color) {
            lineColor = color;
            return self();
        }

        public Builder setLineWidth(Integer width) {
            lineWidth = width;
            return self();
        }

        public Builder setScrollable(boolean scrollable) {
            this.scrollable = scrollable;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public LayoutImmutableContainer build() {
            return new LayoutImmutableContainer(this);
        }

    }

}
