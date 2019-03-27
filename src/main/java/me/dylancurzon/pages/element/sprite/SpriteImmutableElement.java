package me.dylancurzon.pages.element.sprite;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Sprite;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public MutableElement asMutable() {
        return new SpriteMutableElement(margin, sprite);
    }

    public static class Builder extends ImmutableElement.Builder<SpriteImmutableElement, Builder> {

        protected Sprite sprite;

        protected Builder() {}

        protected Builder(SpriteImmutableElement element) {
            sprite = element.sprite;
            margin = element.margin;
        }

        @NotNull
        public Builder setSprite(Sprite sprite) {
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
        public SpriteImmutableElement build() {
            return new SpriteImmutableElement(this);
        }

    }

}
