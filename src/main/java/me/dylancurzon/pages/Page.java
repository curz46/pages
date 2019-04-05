package me.dylancurzon.pages;

import me.dylancurzon.pages.element.container.stacking.Axis;
import me.dylancurzon.pages.element.container.stacking.MutableStackingContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.TickEvent;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

public class Page extends MutableStackingContainer {

    private Vector2i position;
    private Vector2i mousePosition = null;

    public Page(Spacing margin, @Nullable String tag, @Nullable Integer zPosition, @Nullable Axis majorAxis) {
        super(
            null,
            margin,
            tag,
            zPosition == null ? 0 : zPosition,
            majorAxis == null ? Axis.VERTICAL : majorAxis
        );
    }

    public void click(Vector2i position, MouseButton button) {
        post(new MouseClickEvent(position, button));
    }

    public void tick() {
        post(new TickEvent());
    }

    public Vector2i getPosition() {
        return position;
    }

}
