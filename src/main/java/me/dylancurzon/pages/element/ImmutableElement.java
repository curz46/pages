package me.dylancurzon.pages.element;

import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.bus.EventListener;
import me.dylancurzon.pages.util.Spacing;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ImmutableElement {

    protected final Spacing margin;
    protected final Set<EventListener> listeners;
    protected final Set<Consumer<MutableElement>> onCreate;

    protected ImmutableElement(Builder builder) {
        if (builder.margin == null) {
            margin = Spacing.ZERO;
        } else {
            margin = builder.margin;
        }
        listeners = new HashSet<>(builder.listeners);
        onCreate = new HashSet<>(builder.onCreate);
    }

    public abstract MutableElement asMutable();

    public Spacing getMargin() {
        return margin;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder, M extends MutableElement> {

        protected Spacing margin;
        protected Set<EventListener> listeners = new HashSet<>();
        protected Set<Consumer<M>> onCreate = new HashSet<>();

        public B setMargin(Spacing margin) {
            this.margin = margin;
            return self();
        }

        /**
         * Adds a listener to this {@link Builder} which will be subscribed on the {@link MutableElement} upon
         * construction.
         */
        public <K> B subscribe(Class<K> eventType, Consumer<K> consumer) {
            listeners.add(new EventListener<>(eventType, consumer));
            return self();
        }

        /**
         * Equivalent to {@code Builder#subscribe(MouseClickEvent.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnClick(Consumer<MouseClickEvent> consumer) {
            subscribe(MouseClickEvent.class, consumer);
            return self();
        }

        public B doOnCreate(Consumer<M> consumer) {
            onCreate.add(consumer);
            return self();
        }

        public abstract B self();

        public abstract T build();

    }

}
