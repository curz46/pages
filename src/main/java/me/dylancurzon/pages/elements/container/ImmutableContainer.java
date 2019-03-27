package me.dylancurzon.pages.elements.container;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.pages.InteractOptions;

import java.awt.*;
import java.util.Optional;

public interface ImmutableContainer {

    static DefaultImmutableContainer.ContainerBuilder builder() {
        return new DefaultImmutableContainer.ContainerBuilder();
    }

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

    @NotNull
    boolean isCentering();

    @NotNull
    Positioning getPositioning();

    @NotNull
    boolean isScrollable();

    @NotNull
    Optional<Color> getFillColor();

    @NotNull
    Optional<Color> getLineColor();

    @NotNull
    Optional<Integer> getLineWidth();

    @NotNull
    InteractOptions getInteractOptions();

}
