package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public interface ImmutableContainer {

    @NotNull
    Spacing getMargin();

    @NotNull
    Spacing getPadding();

    @NotNull
    Vector2i getSize();

    @NotNull
    Vector2i getMarginedSize();

    @NotNull
    Vector2i getPaddedSize();

    boolean isCentering();

    @NotNull
    Positioning getPositioning();

    @NotNull
    Optional<Color> getFillColor();

    @NotNull
    Optional<Color> getLineColor();

    @NotNull
    Optional<Integer> getLineWidth();

}
