package me.dylancurzon.pages.element.text;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.TextSprite;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

public class TextMutableElement extends MutableElement {

    private final TextImmutableElement immutableElement;
    private TextSprite sprite;

    public TextMutableElement(@Nullable MutableContainer parent,
                              Spacing margin,
                              @Nullable String tag,
                              @Nullable Integer zPosition,
                              TextImmutableElement immutableElement,
                              ElementDecoration decoration) {
        super(parent, margin, tag, zPosition, decoration);
        this.immutableElement = immutableElement;
        sprite = this.immutableElement.getSprite();
    }

    public void setSprite(TextSprite sprite) {
        propagateUpdate();
        this.sprite = sprite;
    }

    public TextSprite getSprite() {
        return sprite;
    }

    @Override
    public Vector2i getSize() {
        return Vector2i.of(sprite.getWidth(), sprite.getHeight());
    }

}
