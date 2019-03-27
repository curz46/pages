package me.dylancurzon.pages.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.SpriteMutableElement;
import me.dylancurzon.pages.util.Sprite;

@Immutable
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
