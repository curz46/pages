package me.dylancurzon.pages.elements.container;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.AlignedElement;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.pages.elements.ImmutableElement;
import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.WrappingMutableElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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

    protected DefaultImmutableContainer(Spacing margin, Consumer<MutableElement> tickConsumer,
                                        List<Function<ImmutableContainer, ImmutableElement>> elements,
                                        Vector2i size, Spacing padding,
                                        Positioning positioning, boolean centering,
                                        boolean scrollable,
                                        Color fillColor, Color lineColor, Integer lineWidth,
                                        Function<MutableElement, WrappingMutableElement> mutator,
                                        InteractOptions interactOptions) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.elements = elements;
        this.size = size;
        if (padding == null) {
            this.padding = Spacing.ZERO;
        } else {
            this.padding = padding;
        }
        if (positioning == null) {
            this.positioning = Positioning.DEFAULT;
        } else {
            this.positioning = positioning;
        }
        this.centering = centering;
        this.scrollable = scrollable;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    @Override
    @NotNull
    public MutableContainer asMutable() {
        List<MutableElement> mutableElements = elements.stream()
            .map(fn -> fn.apply(this))
            .map(ImmutableElement::asMutable)
            .collect(Collectors.toList());
        MutableContainer container = new MutableContainer(super.margin, this, mutableElements) {
            @Override
            public Vector2i calculateSize() {
                Vector2i size = DefaultImmutableContainer.this.size;
                if (size == null || size.getX() == -1 || size.getY() == -1) {
                    Vector2i calculatedSize = Vector2i.of(0, 0);
                    for (MutableElement mut : mutableElements) {
                        Vector2i elementSize = mut.getMarginedSize();
                        calculatedSize = calculatedSize.add(
                            positioning == Positioning.INLINE
                                ? Vector2i.of(elementSize.getX(), 0)
                                : Vector2i.of(0, elementSize.getY())
                        );
                        if (positioning != Positioning.INLINE
                            && calculatedSize.getX() < elementSize.getX()) {
                            calculatedSize = calculatedSize.setX(elementSize.getX());
                        }
                        if (positioning == Positioning.INLINE
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

            @Override
            public List<AlignedElement> draw() {
                List<AlignedElement> elements = new ArrayList<>();
                for (Map.Entry<MutableElement, Vector2i> entry : super.getPositions().entrySet()) {
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
            public void tick() {
                super.tick();
                mutableElements.forEach(MutableElement::tick);
                Consumer<MutableElement> consumer = DefaultImmutableContainer.super.getTickConsumer();
                if (consumer != null) {
                    consumer.accept(this);
                }
            }

            private void applyHighlight(int[] pixels) {
                for (int i = 0; i < pixels.length; i++) {
                    // In order to darken the pixels, we need to convert them to rgb values first so that they
                    // can be affected individually.
                    double factor = 0.7;
                    int value = pixels[i];
                    int a = (value >> 24) & 0xFF;
                    int nr = (int) ((value >> 16 & 0xFF) * factor);
                    int ng = (int) ((value >> 8 & 0xFF) * factor);
                    int nb = (int) ((value & 0xFF) * factor);
                    pixels[i] = (a << 24) | (nr << 16) | (ng << 8) | nb;
                }
            }
        };
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
            return new DefaultImmutableContainer(
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
