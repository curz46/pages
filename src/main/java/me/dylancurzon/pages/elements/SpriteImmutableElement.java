package me.dylancurzon.pages.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.util.Sprite;
import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.SpriteMutableElement;
import me.dylancurzon.pages.elements.mutable.WrappingMutableElement;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public class SpriteImmutableElement extends ImmutableElement {

    private final Sprite sprite;

    public SpriteImmutableElement(Spacing margin, Consumer<MutableElement> tickConsumer,
                                  Function<MutableElement, WrappingMutableElement> mutator,
                                  InteractOptions interactOptions, Sprite animatedSprite) {
        super(margin, tickConsumer, mutator, interactOptions);
        sprite = animatedSprite;
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
        return super.doMutate(new SpriteMutableElement(super.margin, super.interactOptions, sprite) {
            @Override
            public void tick() {
                Consumer<MutableElement> consumer = SpriteImmutableElement.super.tickConsumer;
                if (consumer != null) {
                    consumer.accept(this);
                }
            }
        });
    }

    public static class Builder extends ImmutableElement.Builder<SpriteImmutableElement, Builder> {

        protected Sprite animatedSprite;

        protected Builder() {}

        protected Builder(SpriteImmutableElement element) {
            interactOptions = element.interactOptions;
            animatedSprite = element.sprite;
            tickConsumer = element.tickConsumer;
            mutator = element.mutator;
            margin = element.margin;
        }

        @NotNull
        public Builder setAnimatedSprite(Sprite animatedSprite) {
            this.animatedSprite = animatedSprite;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public SpriteImmutableElement build() {
            return new SpriteImmutableElement(
                super.margin,
                super.tickConsumer,
                super.mutator,
                super.interactOptions,
                animatedSprite
            );

        }

    }

}
