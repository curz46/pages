package me.dylancurzon.pages;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.elements.ImmutableElement;
import me.dylancurzon.pages.elements.container.DefaultImmutableContainer;
import me.dylancurzon.pages.elements.container.ImmutableContainer;
import me.dylancurzon.pages.elements.container.Positioning;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.WrappingMutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PageTemplate extends DefaultImmutableContainer {

    private final Vector2i position;

    protected PageTemplate(Spacing margin, Consumer<MutableElement> tickConsumer,
                           List<Function<ImmutableContainer, ImmutableElement>> elements,
                           Vector2i size, Spacing padding, Positioning positioning, boolean centering,
                           Vector2i position, boolean scrollable,
                           Color fillColor, Color lineColor, Integer lineWidth,
                           Function<MutableElement, WrappingMutableElement> mutator,
                           InteractOptions interactOptions) {
        super(margin, tickConsumer, elements, size, padding, positioning, centering, scrollable, fillColor, lineColor, lineWidth, mutator, interactOptions);
        this.position = position;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NotNull
    public Page asMutable() {
        MutableContainer container = super.asMutable();
        Page page = new Page(this, container);
        container.setParent(page);
        return page;
    }

    @NotNull
    public Vector2i getPosition() {
        return position;
    }

    public static class Builder extends DefaultImmutableContainer.Builder<Builder> {

        private Vector2i position;

        @NotNull
        public Builder setPosition(Vector2i position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public PageTemplate build() {
            if (super.centering && super.elements.size() > 1) {
                throw new RuntimeException(
                    "A centering PageTemplate may only contain a single ImmutableElement!"
                );
            }
            if (super.elements.size() == 0) {
                throw new RuntimeException("Empty PageTemplate is not permitted!");
            }
            if (position == null) {
                throw new RuntimeException("Position is a required attribute!");
            }
            if (size == null) {
                throw new RuntimeException("Size is a required attribute!");
            }

            return new PageTemplate(
                super.margin,
                super.tickConsumer,
                super.elements,
                super.size,
                super.padding,
                super.positioning,
                super.centering,
                position,
                super.scrollable,
                super.fillColor,
                super.lineColor,
                super.lineWidth,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
