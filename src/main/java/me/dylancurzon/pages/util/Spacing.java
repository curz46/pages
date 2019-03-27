package me.dylancurzon.pages.util;

public class Spacing {

    public static final Spacing ZERO = Spacing.of(0);

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    private Spacing(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public static Spacing of(int margin) {
        return new Spacing(margin, margin, margin, margin);
    }

    public static Spacing of(int marginX, int marginY) {
        return new Spacing(marginX, marginY, marginX, marginY);
    }

    public static Spacing of(int marginLeft, int marginTop, int marginRight,
                             int marginBottom) {
        return new Spacing(marginLeft, marginTop, marginRight, marginBottom);
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

}
