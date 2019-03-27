package me.dylancurzon.pages.util;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class Vector2d {

    protected final double x;
    protected final double y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2d of(double x, double y) {
        return new Vector2d(x, y);
    }

    public Vector2d add(Vector2d addend) {
        return new Vector2d(
            x + addend.getX(),
            y + addend.getY()
        );
    }

    public Vector2d add(double addend) {
        return new Vector2d(
            x + addend,
            y + addend
        );
    }

    public Vector2d sub(Vector2d subtrahend) {
        return new Vector2d(
            x - subtrahend.getX(),
            y - subtrahend.getY()
        );
    }

    public Vector2d sub(double subtrahend) {
        return new Vector2d(
            x - subtrahend,
            y - subtrahend
        );
    }

    public Vector2d mul(Vector2d factor) {
        return new Vector2d(
            x * factor.getX(),
            y * factor.getY()
        );
    }

    public Vector2d mul(double factor) {
        return new Vector2d(
            x * factor,
            y * factor
        );
    }

    public Vector2d div(Vector2d divisor) {
        return new Vector2d(
            x / divisor.getX(),
            y / divisor.getY()
        );
    }

    public Vector2d div(double divisor) {
        return new Vector2d(
            x / divisor,
            y / divisor
        );
    }

    public Vector2d floor() {
        return new Vector2d(
            Math.floor(x),
            Math.floor(y)
        );
    }

    /**
     * @return Vector with each component rounded to the nearest integer which is the closest to
     * zero.
     */
    public Vector2d floorAbs() {
        return new Vector2d(
            x >= 0 ? Math.floor(x) : Math.ceil(x),
            y >= 0 ? Math.floor(y) : Math.ceil(y)
        );
    }

    public Vector2d ceil() {
        return new Vector2d(
            Math.ceil(x),
            Math.ceil(y)
        );
    }

    /**
     * @return Vector with each component rounded to the nearest integer which is furthest away
     * from zero.
     */
    public Vector2d ceilAbs() {
        return new Vector2d(
            x >= 0 ? Math.ceil(x) : Math.floor(x),
            y >= 0 ? Math.ceil(y) : Math.floor(y)
        );
    }

    public Vector2d abs() {
        return new Vector2d(
            Math.abs(x),
            Math.abs(y)
        );
    }

    public Vector2i toInt() {
        return new Vector2i(
            (int) x,
            (int) y
        );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Vector2d) {
            Vector2d vector = (Vector2d) object;
            return x == vector.getX() && y == vector.getY();
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return (int) Math.pow(x * 0x1f1f1f1f, y);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

}
