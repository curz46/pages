package me.dylancurzon.pages.element.container.stacking;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AbsoluteMutableContainer extends MutableContainer {

    public AbsoluteMutableContainer(@Nullable MutableContainer parent,
                                    Spacing margin,
                                    @Nullable String tag,
                                    @Nullable Integer zPosition) {
        super(parent, margin, tag, zPosition);
    }

    public void setPositions(Map<MutableElement, Vector2i> positions) {
        this.positions = Objects.requireNonNull(positions);
    }

    @Override
    protected Map<MutableElement, Vector2i> computePositions() {
        // Positions in an AbsoluteMutableContainer are not computed, they are provided.
        return positions;
    }

    /**
     * @return An immutable copy of the children contained by this {@link AbsoluteMutableContainer}. Modifications to
     * a container of this type must be done through {@link this#setPositions(Map)}}.
     */
    @Override
    public List<MutableElement> getChildren() {
        return List.copyOf(positions.keySet());
    }

}
