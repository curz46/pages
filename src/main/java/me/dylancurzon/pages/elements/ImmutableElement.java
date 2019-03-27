package me.dylancurzon.pages.elements;

import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import org.jetbrains.annotations.NotNull;

public abstract class ImmutableElement {

    protected final Spacing margin;

    protected ImmutableElement(Builder builder) {
        if (builder.margin == null) {
            margin = Spacing.ZERO;
        } else {
            margin = builder.margin;
        }
    }

    @NotNull
    public abstract MutableElement asMutable();

    @NotNull
    public Spacing getMargin() {
        return margin;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder> {

        protected Spacing margin;

        @NotNull
        public B setMargin(Spacing margin) {
            this.margin = margin;
            return self();
        }

        @NotNull
        public abstract B self();

        @NotNull
        public abstract T build();

    }

}
