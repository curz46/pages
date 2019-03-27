package me.dylancurzon.pages;

import me.dylancurzon.pages.element.container.DefaultImmutableContainer;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Vector2i;

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
        MutableContainer container = super.asMutable();
        Page page = new Page(this, container);
        container.setParent(page);
        listeners.forEach(page::subscribe);
        return page;
    }

    public Vector2i getPosition() {
        return position;
    }

    public static class Builder extends DefaultImmutableContainer.Builder<Builder> {

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
