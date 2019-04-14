package me.dylancurzon.pages.element.sprite;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Sprite;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ImmutableSpriteElement extends ImmutableElement {

    protected final Sprite sprite;
    @Nullable
    protected final Vector2i forcedSize;

    public ImmutableSpriteElement(Builder builder) {
        super(builder);
        sprite = builder.sprite;
        forcedSize = builder.forcedSize;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            MutableSpriteElement element = new MutableSpriteElement(parent, margin, tag, zIndex, visible, sprite, forcedSize, decoration);
            listeners.forEach(element::subscribe);
            onCreate.forEach(consumer -> consumer.accept(element));
            return element;
        };
    }

    public static class Builder extends ImmutableElement.Builder<ImmutableSpriteElement, Builder, MutableSpriteElement> {

        protected Sprite sprite;
        protected Vector2i forcedSize;

        public Builder setSprite(Sprite sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder setForcedSize(Vector2i forcedSize) {
            this.forcedSize = forcedSize;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public ImmutableSpriteElement build() {
            return new ImmutableSpriteElement(this);
        }

    }

}
