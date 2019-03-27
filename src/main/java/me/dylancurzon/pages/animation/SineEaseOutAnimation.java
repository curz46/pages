package me.dylancurzon.pages.animation;

public class SineEaseOutAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public SineEaseOutAnimation(double min, double max, int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        double x = (((double) super.ticks) / super.duration) * (Math.PI / 2);
        return 1 + Math.sin(x - (Math.PI / 2));
    }

}
