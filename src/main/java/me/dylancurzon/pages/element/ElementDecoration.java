package me.dylancurzon.pages.element;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

public class ElementDecoration {

    @Nullable
    private Color lineColor;
    @Nullable
    private Color fillColor;

    public static Builder builder() {
        return new Builder();
    }

    public static ElementDecoration empty() {
        return new ElementDecoration(null, null);
    }

    public ElementDecoration(@Nullable Color lineColor, @Nullable Color fillColor) {
        this.lineColor = lineColor;
        this.fillColor = fillColor;
    }

    public Optional<Color> getLineColor() {
        return Optional.ofNullable(lineColor);
    }

    public Optional<Color> getFillColor() {
        return Optional.ofNullable(fillColor);
    }

    public static class Builder {

        protected Color lineColor;
        protected Color fillColor;

        public Builder setLineColor(Color lineColor) {
            this.lineColor = lineColor;
            return this;
        }

        public Builder setFillColor(Color fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public ElementDecoration build() {
            return new ElementDecoration(lineColor, fillColor);
        }

    }

}
