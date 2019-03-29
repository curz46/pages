package me.dylancurzon.pages.element.text;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.TextSprite;
import me.dylancurzon.pages.util.Vector2i;

public class TextMutableElement extends MutableElement {

    private final TextImmutableElement immutableElement;
    private TextSprite sprite;

    public TextMutableElement(Spacing margin, String tag, TextImmutableElement immutableElement) {
        super(margin, tag);
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
