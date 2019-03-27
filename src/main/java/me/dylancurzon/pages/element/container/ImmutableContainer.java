package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.Optional;

public interface ImmutableContainer {

    static DefaultImmutableContainer.Builder builder() {
        return new DefaultImmutableContainer.Builder();
    }

    Spacing getMargin();

    Spacing getPadding();

    Vector2i getSize();

    Vector2i getMarginedSize();

    Vector2i getPaddedSize();

    boolean isCentering();

    Positioning getPositioning();

    Optional<Color> getFillColor();

    Optional<Color> getLineColor();

    Optional<Integer> getLineWidth();

}
