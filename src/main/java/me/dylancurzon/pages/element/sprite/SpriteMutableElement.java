package me.dylancurzon.pages.element.sprite;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Sprite;
import me.dylancurzon.pages.util.Vector2i;

public class SpriteMutableElement extends MutableElement {

    protected Sprite sprite;

    public SpriteMutableElement(Spacing margin, Sprite sprite) {
        super(margin);
        this.sprite = sprite;
    }

    public void setSprite(Sprite sprite) {
        propagateUpdate();
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Vector2i getSize() {
        return Vector2i.of(sprite.getWidth(), sprite.getHeight());
    }

}
