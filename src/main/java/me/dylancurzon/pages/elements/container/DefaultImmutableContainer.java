package me.dylancurzon.pages.elements.container;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.elements.ImmutableElement;
import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Immutable
public class DefaultImmutableContainer extends ImmutableElement implements ImmutableContainer {

    public static boolean DEBUG = false;

    protected final List<Function<ImmutableContainer, ImmutableElement>> elements;
    protected final Vector2i size;
    protected final Spacing padding;
    protected final Positioning positioning;
    protected final boolean centering;
    protected final boolean scrollable;
    protected final Color fillColor;
    private final Color lineColor;
    private final Integer lineWidth;

    protected DefaultImmutableContainer(Builder builder) {
        super(builder);
        elements = builder.elements;
        size = builder.size;
        if (builder.padding == null) {
            padding = Spacing.ZERO;
        } else {
            padding = builder.padding;
        }
        if (builder.positioning == null) {
            positioning = Positioning.DEFAULT;
        } else {
            positioning = builder.positioning;
        }
        centering = builder.centering;
        scrollable = builder.scrollable;
        fillColor = builder.fillColor;
        lineColor = builder.lineColor;
        lineWidth = builder.lineWidth;
    }

    @Override
    @NotNull
    public MutableContainer asMutable() {
        List<MutableElement> mutableElements = elements.stream()
            .map(fn -> fn.apply(this))
            .map(ImmutableElement::asMutable)
            .collect(Collectors.toList());
        MutableContainer container = new MutableContainer(margin, this, mutableElements);
        mutableElements.forEach(mut -> mut.setParent(container));
        return container;
    }

    @NotNull
    public List<Function<ImmutableContainer, ImmutableElement>> getElements() {
        return elements;
    }

    @NotNull
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

    @NotNull
    public Spacing getPadding() {
        return padding;
    }

    public boolean isCentering() {
        return centering;
    }

    @Override
    public Positioning getPositioning() {
        return positioning;
    }

    public static class ContainerBuilder extends Builder<ContainerBuilder> {

        @Override
        public ContainerBuilder self() {
            return this;
        }

    }

    public static abstract class Builder<T extends Builder> extends ImmutableElement.Builder<DefaultImmutableContainer, T> {

        protected final List<Function<ImmutableContainer, ImmutableElement>> elements = new ArrayList<>();
        protected Vector2i size;
        protected Spacing padding;
        protected Positioning positioning;
        protected boolean centering;
        protected boolean scrollable;
        protected Color fillColor;
        protected Color lineColor;
        protected Integer lineWidth;

        @NotNull
        public T add(ImmutableElement element) {
            elements.add(page -> element);
            return self();
        }

        @NotNull
        public T add(ImmutableElement... elements) {
            for (ImmutableElement el : elements) {
                add(el);
            }
            return self();
        }

        @NotNull
        public T add(List<ImmutableElement> elements) {
            elements.forEach(this::add);
            return self();
        }

        @NotNull
        public T add(Function<ImmutableContainer, ImmutableElement> fn) {
            elements.add(fn);
            return self();
        }

        @NotNull
        public T setSize(Vector2i size) {
            this.size = size;
            return self();
        }

        @NotNull
        public T setPadding(Spacing padding) {
            this.padding = padding;
            return self();
        }

        @NotNull
        public T setPositioning(Positioning positioning) {
            this.positioning = positioning;
            return self();
        }

        @NotNull
        public T setCentering(boolean centering) {
            this.centering = centering;
            return self();
        }

        @NotNull
        public T setScrollable(boolean scrollable) {
            this.scrollable = scrollable;
            return self();
        }

        @NotNull
        public T setFillColor(Color color) {
            fillColor = color;
            return self();
        }

        @NotNull
        public T setLineColor(Color color) {
            lineColor = color;
            return self();
        }

        @NotNull
        public T setLineWidth(Integer width) {
            lineWidth = width;
            return self();
        }

        @Override
        @NotNull
        public DefaultImmutableContainer build() {
            if (centering && elements.size() > 1 && positioning != Positioning.OVERLAY) {
                throw new RuntimeException(
                    "A centering ImmutableContainer may only contain a single ImmutableElement!"
                );
            }
            return new DefaultImmutableContainer(this);
        }

    }

}
