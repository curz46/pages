package me.dylancurzon.pages.elements.mutable;

import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Sprite;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.NotNull;

public class SpriteMutableElement extends MutableElement {

    protected final Sprite sprite;

    public SpriteMutableElement(Spacing margin, Sprite sprite) {
        super(margin);
        this.sprite = sprite;
    }

    @NotNull
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
