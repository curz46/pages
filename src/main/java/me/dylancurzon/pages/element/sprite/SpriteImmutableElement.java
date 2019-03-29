package me.dylancurzon.pages.element.sprite;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Sprite;

public class SpriteImmutableElement extends ImmutableElement {

    private final Sprite sprite;

    public SpriteImmutableElement(Builder builder) {
        super(builder);
        sprite = builder.sprite;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SpriteImmutableElement element) {
        return new Builder(element);
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public MutableElement asMutable() {
        SpriteMutableElement element = new SpriteMutableElement(margin, tag, sprite);
        listeners.forEach(element::subscribe);
        onCreate.forEach(consumer -> consumer.accept(element));
        return element;
    }

    public static class Builder extends ImmutableElement.Builder<SpriteImmutableElement, Builder, SpriteMutableElement> {

        protected Sprite sprite;

        protected Builder() {}

        protected Builder(SpriteImmutableElement element) {
            sprite = element.sprite;
            margin = element.margin;
        }

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
