package me.dylancurzon.pages.element;

import me.dylancurzon.pages.event.bus.EventListener;
import me.dylancurzon.pages.util.Spacing;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ImmutableElement {

    protected final Spacing margin;
    protected final Set<EventListener> listeners;

    protected ImmutableElement(Builder builder) {
        if (builder.margin == null) {
            margin = Spacing.ZERO;
        } else {
            margin = builder.margin;
        }
        listeners = builder.listeners;
    }

    public abstract MutableElement asMutable();

    public Spacing getMargin() {
        return margin;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder> {

        protected Spacing margin;
        protected Set<EventListener> listeners = new HashSet<>();

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

        public abstract B self();

        public abstract T build();

    }

}
