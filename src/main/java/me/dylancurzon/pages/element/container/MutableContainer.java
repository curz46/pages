package me.dylancurzon.pages.element.container;

import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.MouseScrollEvent;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MutableContainer extends MutableElement {

//    protected final List<MutableElement> children = new ArrayList<>();

    protected Vector2i size;
    protected Vector2i unboundedSize;
    protected Map<MutableElement, Vector2i> positions;

    @Nullable
    protected Vector2i fixedSize;
    @Nullable
    protected Vector2i minimumSize;
    @Nullable
    protected Vector2i maximumSize;
    protected Axis majorAxis;

    protected int offsetX = 0;
    protected int offsetY = 0;

    public MutableContainer(@Nullable MutableContainer parent,
                            Spacing margin,
                            @Nullable String tag,
                            @Nullable Integer zIndex,
                            boolean visible,
                            @Nullable Vector2i fixedSize,
                            @Nullable Vector2i minimumSize,
                            @Nullable Vector2i maximumSize,
                            Axis majorAxis,
                            ElementDecoration decoration) {
        super(parent, margin, tag, zIndex, visible, decoration);

        // Since it's not possible for there to be any children at this point, don't do this
//        positions = computePositions();

        this.fixedSize = fixedSize;
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
        this.majorAxis = Objects.requireNonNull(majorAxis);

        // Whenever this MutableContainer is clicked on, determine whether or not the event should be transferred to
        // this container's children
        doOnClick(event -> {
            Vector2i position = event.getPosition();

            getPositions().forEach((childElement, childPosition) -> {
                // Check the bounds of this childElement
                Vector2i childSize = childElement.getSize();
                if (childPosition.getX() <= position.getX()
                    && childPosition.getY() <= position.getY()
                    && childPosition.getX() + childSize.getX() >= position.getX()
                    && childPosition.getY() + childSize.getY() >= position.getY()) {
                    // Within bounds
                    MouseClickEvent childEvent = new MouseClickEvent(
                        // Make Event relative to the child Element's position
                        position.sub(childPosition),
                        event.getButton()
                    );
                    // Post event
                    childElement.post(childEvent);
                }
            });
        });

        doOnScroll(event -> {
            for (MutableElement child : getChildren()) {
                if (child.getMousePosition() == null) {
                    // Only emit scroll on child if the mouse is currently above it
                    continue;
                }
                child.post(event);
            }
        });

        // Whenever this MutableContainer is "ticked", forward it
        doOnTick(event -> {
            for (MutableElement child : getChildren()) {
                child.post(event);
            }
        });
    }

    /**
     * Recalculates the positions of this {@link MutableContainer}'s children and its size, then propagates this update
     * to the parent.
     */
    @Override
    public void propagateUpdate() {
        if (fixedSize == null) {
            positions = computePositions();
            size = computeSize();
        } else {
            size = computeSize();
            positions = computePositions();
        }

        // Update the mouse position to recalculate the relative position for each child
        setMousePosition(mousePosition);

        super.propagateUpdate();
    }

    @Override
    public void setMousePosition(Vector2i mousePosition) {
        if (Objects.equals(this.mousePosition, mousePosition)) {
            // TODO: If this isn't here, some cyclical bug occurs.
            return;
        }

        super.setMousePosition(mousePosition);
        // Now update children, relatively
        positions.forEach((element, childPosition) -> {
            Vector2i childSize = element.getSize();
            if (mousePosition != null
                && childPosition.getX() <= mousePosition.getX()
                && childPosition.getY() <= mousePosition.getY()
                && childPosition.getX() + childSize.getX() >= mousePosition.getX()
                && childPosition.getY() + childSize.getY() >= mousePosition.getY()) {
                // Within bounds and non-null, set the position
                element.setMousePosition(mousePosition.sub(childPosition));
            } else {
                element.setMousePosition(null);
            }
        });
    }

    public void setFixedSize(Vector2i fixedSize) {
        this.fixedSize = fixedSize;
    }

    public void setMinimumSize(Vector2i minimumSize) {
        this.minimumSize = minimumSize;
    }

    public void setMaximumSize(Vector2i maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void setMajorAxis(Axis majorAxis) {
        this.majorAxis = Objects.requireNonNull(majorAxis);
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public Map<MutableElement, Vector2i> flatten() {
        // Maintain insertion order
        Map<MutableElement, Vector2i> elements = new LinkedHashMap<>();

        positions.forEach((element, position) -> {
            if (element instanceof MutableContainer) {
                Map<MutableElement, Vector2i> containerElements = ((MutableContainer) element).flatten();
                containerElements.forEach((containerElement, containerPosition) ->
                    elements.put(containerElement, position.add(containerPosition)));
            }

            elements.put(element, position);
        });

        elements.put(this, Vector2i.of(0, 0));

        return elements;
    }

    /**
     * Finds all direct or indirect children of this {@link MutableContainer} which have the given tag and are an
     * instance of the given class.
     */
    public <T extends MutableElement> List<T> queryAllElements(String tag, Class<T> clazz) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(clazz);

        List<T> foundElements = new ArrayList<>();

        for (MutableElement element : getChildren()) {
            if (Objects.equals(element.getTag(), tag) && clazz.isAssignableFrom(element.getClass())) {
                //noinspection unchecked
                foundElements.add((T) element);
            }

            if (element instanceof MutableContainer) {
                foundElements.addAll(((MutableContainer) element).queryAllElements(tag, clazz));
            }
        }

        return foundElements;
    }

    /**
     * Finds the first direct or indirect child of this {@link MutableContainer} which has the given tag and is an
     * instance of the given class.
     */
    public <T extends MutableElement> Optional<T> queryElement(String tag, Class<T> clazz) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(clazz);

        for (MutableElement element : getChildren()) {
            if (Objects.equals(element.getTag(), tag) && clazz.isAssignableFrom(element.getClass())) {
//                noinspection unchecked
                return Optional.of((T) element);
            }

            if (element instanceof MutableContainer) {
                Optional<T> firstElement = ((MutableContainer) element).queryElement(tag, clazz);
                if (firstElement.isPresent()) return firstElement;
            }
        }

        return Optional.empty();
    }

    public Vector2i computeSize() {
        // Fixed size is fixed
        if (fixedSize != null) {
            return fixedSize;
        }

        Vector2i size = computeUnboundedSize();

        if (minimumSize != null) {
            if (size.getX() < minimumSize.getX()) {
                size = size.setX(minimumSize.getX());
            }
            if (size.getY() < minimumSize.getY()) {
                size = size.setY(minimumSize.getY());
            }
        }
        if (maximumSize != null) {
            if (size.getX() > maximumSize.getX()) {
                size = size.setX(maximumSize.getX());
            }
            if (size.getY() > maximumSize.getY()) {
                size = size.setY(maximumSize.getY());
            }
        }

        return size;
    }

    public Vector2i computeUnboundedSize() {
        // The goal of size computation is merely to act on the generated positions of this.computePositions()
        // The only way that this Container can expand is rightwards and downwards, so just a case
        // of finding the maximum displacement of an Element's upper bound on an axis
        Vector2i unboundedSize = Vector2i.of(0, 0);

        for (Map.Entry<MutableElement, Vector2i> elementEntry : positions.entrySet()) {
            MutableElement childElement = elementEntry.getKey();
            Vector2i childPosition = elementEntry.getValue();

            Vector2i childSize = childElement.getSize();
            Spacing childMargin = childElement.getMargin();
            // Only need to account for right + bottom Margin, since the rest should be accounted for by the position
            // already
            Vector2i childUpperBound = Vector2i.of(
                childPosition.getX() + childSize.getX() + childMargin.getRight(),
                childPosition.getY() + childSize.getY() + childMargin.getTop()
            );

            // If greater than the current size, then size increase to contain this Element
            if (unboundedSize.getX() < childUpperBound.getX()) {
                unboundedSize = unboundedSize.setX(childUpperBound.getX());
            }
            if (unboundedSize.getY() < childUpperBound.getY()) {
                unboundedSize = unboundedSize.setY(childUpperBound.getY());
            }
        }

        return unboundedSize;
    }

    /**
     * @return A map of each MutableElement (in {@link this#getChildren()} and its calculated position. It should factor in
     * if the {@link MutableContainer} is inline, padded and respects each MutableElement's margin.
     */
    protected abstract Map<MutableElement, Vector2i> computePositions();

    public abstract List<MutableElement> getChildren();

    public Map<MutableElement, Vector2i> getPositions() {
        if (positions == null) {
            positions = computePositions();
        }
        return positions;
    }

    @Override
    public Vector2i getSize() {
        if (size != null) {
            return size;
        }

        if (fixedSize != null) {
            size = fixedSize;
            return size;
        }

        unboundedSize = getUnboundedSize();
        size = unboundedSize;

        if (minimumSize != null) {
            if (size.getX() < minimumSize.getX()) {
                size = size.setX(minimumSize.getX());
            }
            if (size.getY() < minimumSize.getY()) {
                size = size.setY(minimumSize.getY());
            }
        }

        if (maximumSize != null) {
            if (size.getX() > maximumSize.getX()) {
                size = size.setX(maximumSize.getX());
            }
            if (size.getY() > maximumSize.getY()) {
                size = size.setY(maximumSize.getY());
            }
        }

        return size;
    }

    /**
     * @return The size of this container if there were no restrictions on its size by {@link this#fixedSize},
     * {@link this#minimumSize} or {@link this#maximumSize}. For some containers where {@link this#fixedSize} is a
     * required attribute, this will be equivalent to {@link this#size}. Otherwise, it will be determined by the
     * extreme positions of the children of this container.
     */
    public Vector2i getUnboundedSize() {
        if (unboundedSize == null) {
            unboundedSize = computeUnboundedSize();
        }
        return unboundedSize;
    }

    public Optional<Vector2i> getFixedSize() {
        return Optional.ofNullable(fixedSize);
    }

    public Optional<Vector2i> getMinimumSize() {
        return Optional.ofNullable(minimumSize);
    }

    public Optional<Vector2i> getMaximumSize() {
        return Optional.ofNullable(maximumSize);
    }

    public Axis getMajorAxis() {
        return majorAxis;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

}
