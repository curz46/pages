package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ImmutableElement;
import me.dylancurzon.pages.event.MouseScrollEvent;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Math.*;

public abstract class ImmutableContainer extends ImmutableElement {

    /**
     * A velocity-based SmoothScrollHandler which "bounces back" from the real bounds of the container to provide some
     * user-friendly feedback.
     */
    public static Function<MutableContainer, ScrollHandler> ELASTIC_SCROLLING = container -> new ScrollHandler() {
        private final double VELOCITY_OFFSET_FACTOR = 1;
        private final double VELOCITY_FRICTION = 0.1;
        private final double VELOCITY_MINIMUM = 0.05;

        private final double VELOCITY_BOUNCE_FACTOR = 0.05;
        private final double VELOCITY_BOUNCE_FRICTION = 0.5;
        // Store a copy of the majorOffset as a double so that we're more accurate
        private double majorOffset = 0.0;
        private double velocity = 0.0;

        @Override
        public void tick() {
            if (velocity == 0) return;

            majorOffset += velocity;

            // Add "bounce back" effect
            double minimumBoundDelta = -majorOffset;
            Vector2i boundedSize = container.getSize();
            Vector2i unboundedSize = container.getUnboundedSize();
            Vector2i maximumOffset = boundedSize.sub(unboundedSize);
            int maximumValue = container.getMajorAxis() == Axis.VERTICAL
                ? maximumOffset.getY()
                : maximumOffset.getX();
            double maximumBoundDelta = majorOffset - maximumValue;
            if (minimumBoundDelta < 0 || maximumBoundDelta < 0) {
                if (abs(minimumBoundDelta) < abs(maximumBoundDelta)) {
                    velocity += minimumBoundDelta * VELOCITY_BOUNCE_FACTOR;
                } else {
                    velocity -= maximumBoundDelta * VELOCITY_BOUNCE_FACTOR;
                }
                velocity *= (1.0 - VELOCITY_BOUNCE_FRICTION);
            } else {
                velocity *= (1.0 - VELOCITY_FRICTION);
            }

            if (abs(velocity) < VELOCITY_MINIMUM) {
                // Set to zero when we reach the minimum so that we eventually completely stop scrolling
                velocity = 0;
            }

            // Update container majorOffset
            if (container.getMajorAxis() == Axis.VERTICAL) {
                container.setOffsetY((int) Math.round(majorOffset));
            } else {
                container.setOffsetX((int) Math.round(majorOffset));
            }
            // TODO: Calling this every tick means any rendering implementation will have to work very hard to render a
            //       scrolling container. The only real way to fix this is partial updates, so rendering impl.s can
            //       update only what has changed.
            container.propagateUpdate();
        }

        @Override
        public void scroll(double offset) {
            velocity += offset * VELOCITY_OFFSET_FACTOR;
        }
    };

    @Nullable
    protected final Vector2i fixedSize;
    @Nullable
    protected final Vector2i minimumSize;
    @Nullable
    protected final Vector2i maximumSize;
    protected final Axis majorAxis;

    protected ImmutableContainer(Builder builder) {
        super(builder);
        fixedSize = builder.fixedSize;
        minimumSize = builder.minimumSize;
        maximumSize = builder.maximumSize;
        majorAxis = builder.majorAxis == null ? Axis.VERTICAL : builder.majorAxis;
    }

    public abstract List<ImmutableElement> getChildren();

    public Optional<Vector2i> getFixedSize() {
        return Optional.ofNullable(fixedSize);
    }

    public Optional<Vector2i> getMinimumSize() {
        return Optional.ofNullable(minimumSize);
    }

    public Optional<Vector2i> getMaximumSize() {
        return Optional.ofNullable(maximumSize);
    }

    public static abstract class Builder<
        T extends ImmutableContainer,
        B extends Builder,
        M extends MutableContainer> extends ImmutableElement.Builder<T, B, M> {

        protected Vector2i fixedSize;
        protected Vector2i minimumSize;
        protected Vector2i maximumSize;
        protected Axis majorAxis;

        public B setMinimumSize(Vector2i minimumSize) {
            this.minimumSize = minimumSize;
            return self();
        }

        public B setMaximumSize(Vector2i maximumSize) {
            this.maximumSize = maximumSize;
            return self();
        }

        public B setFixedSize(Vector2i fixedSize) {
            this.fixedSize = fixedSize;
            return self();
        }

        public B setMajorAxis(Axis majorAxis) {
            this.majorAxis = majorAxis;
            return self();
        }

        public B fillAllocatedSize() {
            doOnCreate(element -> element.doOnSizeAllocate(event -> {
                element.setFixedSize(event.getAllocatedSize());
                element.propagateUpdate();
            }));
            return self();
        }

        public B fillParentContainer() {
            doOnCreate(element -> {
                element.setFixedSize(element.getParent() == null
                    ? Vector2i.of(0, 0)
                    : element.getParent().getSize());
                element.propagateUpdate();
            });
            return self();
        }

        /**
         * Configures a {@link this#doOnScroll(Consumer)} subscriber which affects the major offset (the offset of the
         * major axis) of any {@link MutableContainer} created when {@link ImmutableContainer#asMutable()} is called
         * such that the container allows scrolling on its major axis. This scroll handler will not allow positive
         * scrolling when the major offset is zero and will not allow negative scrolling when the major offset is such
         * that further negative scrolling would go out of the {@link MutableContainer}'s bounds.
         * @param factor The factor that the offset given by {@link MouseScrollEvent#getOffset()} is multiplied by in
         *               order to obtain the transformation of the major offset of the {@link MutableContainer}.
         */
        public B setScrolling(double factor) {
            // TODO: This should be a helper function for a ScrollHandler
            doOnCreate(element -> element.doOnScroll(event -> {
                System.out.println("Scroll: " + event.getOffset());
                // Get major offset
                int majorOffset = element.getMajorAxis() == Axis.VERTICAL
                    ? element.getOffsetY()
                    : element.getOffsetX();

                // Change major offset
                majorOffset += factor * event.getOffset();

                // Check within bounds
                // Note: when writing this code, a misconception resulted in initially writing it wrong. The majorOffset
                // is the amount to offset each child element, so it is equivalent to the delta value from the "view"'s
                // origin to the "real" origin and NOT from the "real" to the "view". This results in all valid offsetY
                // values being non-positive. The concept of a View is quite an intuitive one and ideally the code would
                // be refactored to more cleanly support containers being larger than their respective "view".
                if (majorOffset > 0) majorOffset = 0;
                // When the majorOffset is equal to the relevant component of (unboundedSize - boundedSize), then
                // further scrolling should be cancelled.
                Vector2i boundedSize = element.getSize();
                Vector2i unboundedSize = element.getUnboundedSize();
                Vector2i maximumOffset = boundedSize.sub(unboundedSize);
                int maximumValue = element.getMajorAxis() == Axis.VERTICAL
                    ? maximumOffset.getY()
                    : maximumOffset.getX();
                if (majorOffset < maximumValue) majorOffset = maximumValue;

                // Update major offset
                if (element.getMajorAxis() == Axis.VERTICAL) {
                    element.setOffsetY(majorOffset);
                } else {
                    element.setOffsetX(majorOffset);
                }
                element.propagateUpdate();
            }));
            return self();
        }

        /**
         * Configures a {@link ScrollHandler} for each created {@link MutableContainer} using the given handlerFunction.
         * This is just a helper method to subscribe {@link ScrollHandler#tick()} to {@link this#doOnTick(Runnable)}
         * and {@link ScrollHandler#scroll(double)} to {@link this#doOnScroll(Consumer)} such that it can provide
         * easy-on-the-eyes transformations, e.g. with velocity-based scrolling.
         */
        public B setScrolling(Function<MutableContainer, ScrollHandler> handlerFunction) {
            doOnCreate(element -> {
                ScrollHandler handler = handlerFunction.apply(element);
                element.doOnTick(handler::tick);
                element.doOnScroll(event -> handler.scroll(event.getOffset()));
            });
            return self();
        }

    }

    public interface ScrollHandler {

        void tick();

        void scroll(double offset);

    }

}
