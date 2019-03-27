package me.dylancurzon.pages.elements.container;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.elements.ImmutableElement;
import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.WrappingMutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.dylancurzon.pages.elements.container.Positioning.INLINE;

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


    private LayoutImmutableContainer(Spacing margin, Consumer<MutableElement> tickConsumer,
                                     List<Pair<Integer, Function<ImmutableContainer, ImmutableElement>>> elements,
                                     Vector2i size, Spacing padding,
                                     Positioning positioning, boolean centering,
                                     boolean scrollable, Color fillColor, Color lineColor,
                                     Integer lineWidth,
                                     Function<MutableElement, WrappingMutableElement> mutator,
                                     InteractOptions interactOptions) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.elements = elements;
        this.size = size;
        this.positioning = positioning;
        this.centering = centering;
        if (padding == null) {
            this.padding = Spacing.ZERO;
        } else {
            this.padding = padding;
        }
        this.scrollable = scrollable;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NotNull
    public MutableContainer asMutable() {
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
        return ImmutableContainer.builder()
            .setSize(size)
            .setPadding(padding)
            .setPositioning(positioning)
            .setScrollable(scrollable)
            .setFillColor(fillColor)
            .setLineColor(lineColor)
            .setLineWidth(lineWidth)
            .add(wrappedElements)
            .build()
            .asMutable();
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
                super.margin.getLeft() + super.margin.getRight(),
                super.margin.getBottom() + super.margin.getTop()
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
    @NotNull
    public boolean isScrollable() {
        return scrollable;
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

    public static class Builder extends ImmutableElement.Builder<LayoutImmutableContainer, Builder> {

        private final List<Pair<Integer, Function<ImmutableContainer, ImmutableElement>>> elements = new ArrayList<>();
        private Vector2i size;
        private Spacing padding;
        private Positioning positioning;
        private boolean centering;
        private boolean scrollable;
        private Color fillColor;
        private Color lineColor;
        private Integer lineWidth;

        @NotNull
        public Builder add(int ratio, ImmutableElement element) {
            elements.add(Pair.of(ratio, page -> element));
            return this;
        }

        @NotNull
        public Builder add(int ratio,
                           Function<ImmutableContainer, ImmutableElement> fn) {
            elements.add(Pair.of(ratio, fn));
            return this;
        }

        @NotNull
        public Builder setSize(Vector2i size) {
            this.size = size;
            return this;
        }

        @NotNull
        public Builder setPadding(Spacing padding) {
            this.padding = padding;
            return this;
        }

        @NotNull
        public Builder setPositioning(Positioning positioning) {
            this.positioning = positioning;
            return this;
        }

        @NotNull
        public Builder setCentering(boolean centering) {
            this.centering = centering;
            return this;
        }

        @NotNull
        public Builder setFillColor(Color color) {
            fillColor = color;
            return self();
        }

        @NotNull
        public Builder setLineColor(Color color) {
            lineColor = color;
            return self();
        }

        @NotNull
        public Builder setLineWidth(Integer width) {
            lineWidth = width;
            return self();
        }

        @NotNull
        public Builder setScrollable(boolean scrollable) {
            this.scrollable = scrollable;
            return this;
        }

        @Override
        @NotNull
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public LayoutImmutableContainer build() {
            return new LayoutImmutableContainer(
                super.margin,
                super.tickConsumer,
                elements,
                size,
                padding,
                positioning,
                centering,
                scrollable,
                fillColor,
                lineColor,
                lineWidth,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
