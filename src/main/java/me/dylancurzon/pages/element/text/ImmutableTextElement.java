package me.dylancurzon.pages.element.text;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.TextSprite;

import java.util.function.Function;

public class ImmutableTextElement extends ImmutableElement {

    private final TextSprite sprite;

    protected ImmutableTextElement(Builder builder) {
        super(builder);
        sprite = builder.sprite;
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            MutableTextElement element = new MutableTextElement(parent, margin, tag, zIndex, this, decoration);
            listeners.forEach(element::subscribe);
            onCreate.forEach(consumer -> consumer.accept(element));
            return element;
        };
    }

    public TextSprite getSprite() {
        return sprite;
    }

    public static class Builder extends ImmutableElement.Builder<ImmutableTextElement, Builder, MutableTextElement> {

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
        public ImmutableTextElement build() {
            return new ImmutableTextElement(this);
        }

    }

}
