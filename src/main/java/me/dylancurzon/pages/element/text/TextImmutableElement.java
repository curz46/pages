package me.dylancurzon.pages.element.text;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.TextSprite;

public class TextImmutableElement extends ImmutableElement {

    private final TextSprite sprite;

    protected TextImmutableElement(Builder builder) {
        super(builder);
        sprite = builder.sprite;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public MutableElement asMutable() {
        TextMutableElement element = new TextMutableElement(margin, tag, this);
        listeners.forEach(element::subscribe);
        onCreate.forEach(consumer -> consumer.accept(element));
        return element;
    }

    public TextSprite getSprite() {
        return sprite;
    }

    public static class Builder extends ImmutableElement.Builder<TextImmutableElement, Builder, TextMutableElement> {

        private TextSprite sprite;

        public Builder setText(TextSprite sprite) {
            this.sprite = sprite;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public TextImmutableElement build() {
            return new TextImmutableElement(this);
        }

    }

}
