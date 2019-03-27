package me.dylancurzon.pages.animation;

public class QuarticEaseInAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public QuarticEaseInAnimation(double min, double max, int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        double x = ((double) ticks) / duration;
        return 1 - Math.pow(x - 1, 4);
    }

}
