package me.dylancurzon.pages.animation;

public class SineEaseInAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public SineEaseInAnimation(double min, double max, int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        // When ticks=0, this will equal 0. As ticks=duration, this will equal Math.PI / 2.
        double x = (((double) ticks) / duration) * (Math.PI / 2);
        // Between 0 and Math.PI / 2, the sine function will return a value between 0 and 1. The
        // proceeding calculation expands and offsets this range to the desired minimum and maximum
        // values.
        return (max - min) * Math.sin(x) + min;
    }

}
