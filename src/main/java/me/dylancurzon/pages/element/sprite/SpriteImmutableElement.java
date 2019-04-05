package me.dylancurzon.pages.element.sprite;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Sprite;

import java.util.function.Function;

public class SpriteImmutableElement extends ImmutableElement {

    private final Sprite sprite;

    public SpriteImmutableElement(Builder builder) {
        super(builder);
        sprite = builder.sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            SpriteMutableElement element = new SpriteMutableElement(parent, margin, tag, zPosition, sprite, decoration);
            listeners.forEach(element::subscribe);
            onCreate.forEach(consumer -> consumer.accept(element));
            return element;
        };
    }

    public static class Builder extends ImmutableElement.Builder<SpriteImmutableElement, Builder, SpriteMutableElement> {

        protected Sprite sprite;

        public Builder setSprite(Sprite sprite) {
            this.sprite = sprite;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public SpriteImmutableElement build() {
            return new SpriteImmutableElement(this);
        }

    }

}
