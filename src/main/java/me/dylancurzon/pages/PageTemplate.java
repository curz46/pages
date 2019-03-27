package me.dylancurzon.pages;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.DefaultImmutableContainer;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;
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

    @Override
    public Page asMutable() {
        // TODO: This code is copy-pasted from DefaultImmutableContainer, but I don't know how to abstract it
        // Note: it's probably more effort than it's worth
        List<MutableElement> mutableElements = elements.stream()
            .map(fn -> fn.apply(this))
            .map(ImmutableElement::asMutable)
            .collect(Collectors.toList());
        Page page = new Page(margin, this, mutableElements);
        mutableElements.forEach(mut -> mut.setParent(page));
        listeners.forEach(page::subscribe);
        onCreate.forEach(consumer -> consumer.accept(page));
        return page;
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
