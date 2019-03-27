package me.dylancurzon.pages.elements.mutable;

import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Sprite;
import me.dylancurzon.pages.util.Vector2i;

public abstract class SpriteMutableElement extends MutableElement {

    protected final Sprite sprite;

    protected SpriteMutableElement(Spacing margin, InteractOptions interactOptions, Sprite sprite) {
        super(margin, interactOptions);
        this.sprite = sprite;
    }

    @Override
    public Vector2i calculateSize() {
        return Vector2i.of(
            sprite.getWidth(),
            sprite.getHeight()
        );
    }

    public Sprite getSprite() {
        return sprite;
    }

}
