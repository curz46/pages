package me.dylancurzon.pages;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.DefaultImmutableContainer;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageTemplate extends DefaultImmutableContainer {

    private final Vector2i position;

    protected PageTemplate(Builder builder) {
        super(builder);
        position = builder.position;
    }

    public static Builder builder() {
        return new Builder();
    }

    // TODO: Maybe this shouldn't be public?
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            Page page = new Page(margin, tag, zPosition, this);
            List<MutableElement> children = elements.stream()
                .map(fn -> fn.apply(this))
                .map(element -> element.asMutable(page))
                .collect(Collectors.toList());
            page.getChildren().addAll(children);

            listeners.forEach(page::subscribe);
            onCreate.forEach(consumer -> consumer.accept(page));

            return page;
        };
    }

    /**
     * Creates a {@link Page} by recursively calling {@link ImmutableElement#asMutable} such that the tree is replicated
     * as a {@link MutableElement}.
     */
    public Page create() {
        return (Page) asMutable().apply(null);
    }

    public Vector2i getPosition() {
        return position;
    }

    public static class Builder extends AbstractBuilder<Builder> {

        private Vector2i position = Vector2i.of(0, 0);

        public Builder setPosition(Vector2i position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public PageTemplate build() {
            if (centering && elements.size() > 1) {
                throw new RuntimeException(
                    "A centering PageTemplate may only contain a single ImmutableElement!"
                );
            }
            if (size == null) {
                throw new RuntimeException("Size is a required attribute!");
            }
            return new PageTemplate(this);
        }

    }

}
