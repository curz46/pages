package me.dylancurzon.pages.element;

import me.dylancurzon.pages.util.Spacing;

public abstract class ImmutableElement {

    protected final Spacing margin;

    protected ImmutableElement(Builder builder) {
        if (builder.margin == null) {
            margin = Spacing.ZERO;
        } else {
            margin = builder.margin;
        }
    }

    public abstract MutableElement asMutable();

    public Spacing getMargin() {
        return margin;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder> {

        protected Spacing margin;

        public B setMargin(Spacing margin) {
            this.margin = margin;
            return self();
        }

        public abstract B self();

        public abstract T build();

    }

}
