package me.dylancurzon.pages.util;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class Vector2i {

    private final int x;
    private final int y;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2i of(int x, int y) {
        return new Vector2i(x, y);
    }

    public Vector2i setX(int value) {
        return new Vector2i(value, y);
    }

    public Vector2i setY(int value) {
        return new Vector2i(x, value);
    }

    public Vector2i add(Vector2i addend) {
        return new Vector2i(
            x + addend.getX(),
            y + addend.getY()
        );
    }

    public Vector2i add(int addend) {
        return new Vector2i(
            x + addend,
            y + addend
        );
    }

    public Vector2i sub(Vector2i subtrahend) {
        return new Vector2i(
            x - subtrahend.getX(),
            y - subtrahend.getY()
        );
    }

    public Vector2i sub(int subtrahend) {
        return new Vector2i(
            x - subtrahend,
            y - subtrahend
        );
    }

    public Vector2i mul(Vector2i factor) {
        return new Vector2i(
            x * factor.getX(),
            y * factor.getY()
        );
    }

    public Vector2i mul(int factor) {
        return new Vector2i(
            x * factor,
            y * factor
        );
    }

    public Vector2i integerDiv(Vector2i divisor) {
        return new Vector2i(
            x / divisor.getX(),
            y / divisor.getY()
        );
    }

    public Vector2i integerDiv(Vector2d divisor) {
        return integerDiv(divisor.toInt());
    }

    public Vector2i integerDiv(int divisor) {
        return new Vector2i(
            x / divisor,
            y / divisor
        );
    }

    public Vector2d div(Vector2d divisor) {
        return new Vector2d(
            ((double) x) / divisor.getX(),
            ((double) y) / divisor.getY()
        );
    }

    public Vector2d div(double divisor) {
        return new Vector2d(
            ((double) x) / divisor,
            ((double) y) / divisor
        );
    }

    public Vector2i abs() {
        return new Vector2i(
            Math.abs(x),
            Math.abs(y)
        );
    }

    public Vector2d normalize() {
        double value = absv();
        return new Vector2d(
            ((double) x) / value,
            ((double) y) / value
        );
    }

    public double absv() {
        return Math.sqrt((x * x) + (y * y));
    }

    public Vector2d toDouble() {
        return new Vector2d(
            x,
            y
        );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Vector2i) {
            Vector2i vector = (Vector2i) object;
            return x == vector.getX() && y == vector.getY();
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return x < y
            ? y * y + x
            : x * x + x + y;

    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

}
