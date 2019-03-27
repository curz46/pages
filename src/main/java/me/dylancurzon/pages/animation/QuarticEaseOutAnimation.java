package me.dylancurzon.pages.animation;

public class QuarticEaseOutAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public QuarticEaseOutAnimation(double min, double max, int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        double x = ((double) ticks) / duration;
        return Math.pow(x, 4);
    }

}
