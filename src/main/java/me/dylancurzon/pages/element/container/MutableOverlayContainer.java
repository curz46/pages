package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MutableOverlayContainer extends MutableContainer {

    private final List<MutableElement> children = new ArrayList<>();
    protected final boolean centerOnX;
    protected final boolean centerOnY;

    public MutableOverlayContainer(@Nullable MutableContainer parent,
                                    Spacing margin,
                                    @Nullable String tag,
                                    @Nullable Integer zIndex,
                                    boolean visible,
                                    boolean centerOnX,
                                    boolean centerOnY,
                                    @Nullable Vector2i fixedSize,
                                    @Nullable Vector2i minimumSize,
                                    @Nullable Vector2i maximumSize,
                                    Axis majorAxis,
                                    ElementDecoration decoration) {
        super(parent, margin, tag, zIndex, visible, fixedSize, minimumSize, maximumSize, majorAxis, decoration);
        this.centerOnX = centerOnX;
        this.centerOnY = centerOnY;
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        // Set the allocated size for all elements
        for (MutableElement element : children) {
            element.setAllocatedSize(getSize());
        }

        Vector2d midpoint = getSize().div(2);
        return children.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                child -> {
                    Vector2i centeredPosition = midpoint.sub(child.getSize().div(2)).toInt();
                    return Vector2i.of(
                        centerOnX ? centeredPosition.getX() : 0,
                        centerOnY ? centeredPosition.getY() : 0
                    ).add(Vector2i.of(offsetX, offsetY));
                },
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new
            ));
    }

    @Override
    public List<MutableElement> getChildren() {
        return children;
    }

}
