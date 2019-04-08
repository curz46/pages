package me.dylancurzon.pages.element.sprite;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Sprite;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

public class MutableSpriteElement extends MutableElement {

    protected Sprite sprite;

    public MutableSpriteElement(@Nullable MutableContainer parent,
                                Spacing margin,
                                @Nullable String tag,
                                @Nullable Integer zPosition,
                                Sprite sprite,
                                ElementDecoration decoration) {
        super(parent, margin, tag, zPosition, decoration);
        this.sprite = sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        propagateUpdate();
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Vector2i getSize() {
        return Vector2i.of(sprite.getWidth(), sprite.getHeight());
    }

}
