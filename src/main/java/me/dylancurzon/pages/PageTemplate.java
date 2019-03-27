package me.dylancurzon.pages;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.elements.container.DefaultImmutableContainer;
import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.util.Vector2i;

public class PageTemplate extends DefaultImmutableContainer {

    private final Vector2i position;

    protected PageTemplate(Builder builder) {
        super(builder);
        position = builder.position;
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

            return new PageTemplate(this);
        }

    }

}
