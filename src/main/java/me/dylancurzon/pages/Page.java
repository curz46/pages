package me.dylancurzon.pages;

import me.dylancurzon.pages.elements.mutable.MutableContainer;
import me.dylancurzon.pages.animation.Animation;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.util.Vector2i;

import java.util.List;
import java.util.Map;

public class Page extends MutableContainer {

    public static final boolean DEBUG_CONTAINERS = false;

    private final PageTemplate template;
    private final MutableContainer container;

    private Vector2i position;
    private TransformHandler transform;

    private Vector2i mousePosition = Vector2i.of(0, 0);

    protected Page(PageTemplate template, MutableContainer container) {
        super(template.getMargin(), template, container.getElements());
        this.template = template;
        this.container = container;

        position = this.template.getPosition();
    }

    public void setMousePosition(Vector2i position) {
        mousePosition = position;
    }

    @Override
    public void scroll(double amount) {
        container.scroll(amount);
    }

    public void transform(Vector2i position) {
        this.position = position;
    }

    public void transform(Vector2i destination, Animation animation) {
        transform = new TransformHandler(position, destination, animation);
    }

    @Override
    public Vector2i calculateSize() {
        return template.getSize();
    }

    @Override
    public List<AlignedElement> draw() {
        return container.draw();
    }

    /**
     * @param position The position of the click event on the screen. Will only fire if within this Page's bounds.
     */
    @Override
    public void click(Vector2i position) {
        Vector2i relative = position.sub(this.position);
        container.click(relative);
    }

    @Override
    public Vector2i getMousePosition(MutableElement element) {
        // A page is a wrapper for a container. The container has the same position.
        // final Vector2i position = this.calculatePositions().get(element);
        if (mousePosition == null) return null;
        return mousePosition.sub(position);
    }

    public Map<MutableElement, Vector2i> getPositions() {
        return container.getPositions();
    }

    @Override
    public void tick() {
        container.tick();
    }

    public static class TransformHandler {

        private final Vector2i initialPosition;
        private final Vector2i destination;
        private final Animation animation;

        public TransformHandler(Vector2i initialPosition, Vector2i destination,
                                Animation animation) {
            this.initialPosition = initialPosition;
            this.destination = destination;
            this.animation = animation;
        }

        public void tick() {
            animation.tick();
        }

        public Vector2i getPosition() {
            double progress = animation.determineValue();
            Vector2i delta =
                destination.sub(initialPosition)
                    .toDouble()
                    .mul(progress)
                    .floor().toInt();
            return initialPosition.add(delta);
        }

        public boolean isCompleted() {
            return animation.isCompleted();
        }

    }

}
