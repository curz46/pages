package me.dylancurzon.pages.elements.mutable;

import me.dylancurzon.pages.util.TextSprite;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.pages.elements.TextImmutableElement;
import me.dylancurzon.pages.util.Spacing;

import java.util.function.Consumer;

public class TextMutableElement extends MutableElement {

    private final TextImmutableElement immutableElement;
    private TextSprite sprite;

    public TextMutableElement(Spacing margin, TextImmutableElement immutableElement) {
        super(margin, immutableElement.getInteractOptions());
        this.immutableElement = immutableElement;
        sprite = this.immutableElement.getSprite();
    }

    public void setSprite(TextSprite sprite) {
        this.sprite = sprite;
    }

    public TextSprite getSprite() {
        return sprite;
    }

    @Override
    public Vector2i calculateSize() {
        return Vector2i.of(sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void tick() {
        Consumer<MutableElement> consumer = immutableElement.getTickConsumer();
        if (consumer != null) {
            consumer.accept(this);
        }
    }

}
