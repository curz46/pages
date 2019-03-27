package me.dylancurzon.pages.elements;

import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.TextMutableElement;
import me.dylancurzon.pages.util.TextSprite;
import org.jetbrains.annotations.NotNull;

public class TextImmutableElement extends ImmutableElement {

    private final TextSprite sprite;

    protected TextImmutableElement(Builder builder) {
        super(builder);
        sprite = builder.sprite;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NotNull
    public MutableElement asMutable() {
        return new TextMutableElement(margin, this);
    }

    @NotNull
    public TextSprite getSprite() {
        return sprite;
    }

    public static class Builder extends ImmutableElement.Builder<TextImmutableElement, Builder> {

        private TextSprite sprite;

        @NotNull
        public Builder setText(TextSprite sprite) {
            this.sprite = sprite;
            return this;
        }

        @NotNull
        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public TextImmutableElement build() {
            return new TextImmutableElement(this);
        }

    }

}
