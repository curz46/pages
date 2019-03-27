package me.dylancurzon.pages.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.util.TextSprite;
import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.TextMutableElement;
import me.dylancurzon.pages.elements.mutable.WrappingMutableElement;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public class TextImmutableElement extends ImmutableElement {

    private final TextSprite sprite;

    protected TextImmutableElement(Spacing margin, Consumer<MutableElement> tickConsumer,
                                   TextSprite sprite,
                                   Function<MutableElement, WrappingMutableElement> mutator,
                                   InteractOptions interactOptions) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.sprite = sprite;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NotNull
    public MutableElement asMutable() {
        return super.doMutate(new TextMutableElement(super.margin, this));
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

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public TextImmutableElement build() {
            return new TextImmutableElement(
                super.margin,
                super.tickConsumer,
                sprite,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
