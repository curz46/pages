package me.dylancurzon.pages.element;

import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.event.*;
import me.dylancurzon.pages.event.bus.EventListener;
import me.dylancurzon.pages.util.Spacing;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ImmutableElement {

    protected final Spacing margin;
    @Nullable
    protected final String tag;
    @Nullable
    protected final Integer zPosition;

    protected final ElementDecoration decoration;

    protected final Set<EventListener> listeners;
    protected final Set<Consumer<MutableElement>> onCreate;

    protected ImmutableElement(Builder builder) {
        if (builder.margin == null) {
            margin = Spacing.ZERO;
        } else {
            margin = builder.margin;
        }
        tag = builder.tag;
        zPosition = builder.zPosition;
        decoration = Objects.requireNonNull(builder.decoration);

        listeners = new HashSet<>(builder.listeners);
        onCreate = new HashSet<>(builder.onCreate);
    }

    public MutableElement asMutable(MutableContainer parent) {
        return asMutable().apply(parent);
    }

    public abstract Function<MutableContainer, MutableElement> asMutable();

    public Spacing getMargin() {
        return margin;
    }

    public Set<EventListener> getListeners() {
        return listeners;
    }

    public Set<Consumer<MutableElement>> getOnCreateConsumers() {
        return onCreate;
    }

    @Nullable
    public String getTag() {
        return tag;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder, M extends MutableElement> {

        protected Spacing margin;
        protected Set<EventListener> listeners = new HashSet<>();
        protected Set<Consumer<M>> onCreate = new HashSet<>();
        protected String tag;
        protected Integer zPosition;
        protected ElementDecoration decoration = ElementDecoration.empty();

        public B setMargin(Spacing margin) {
            this.margin = margin;
            return self();
        }

        public B setTag(String tag) {
            this.tag = tag;
            return self();
        }

        public B setZ(Integer zPosition) {
            this.zPosition = zPosition;
            return self();
        }

        public B setDecoration(ElementDecoration decoration) {
            this.decoration = decoration;
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
         * Equivalent to {@code Builder#subscribe(UpdateEvent.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnUpdate(Consumer<UpdateEvent> consumer) {
            subscribe(UpdateEvent.class, consumer);
            return self();
        }

        /**
         * Equivalent to {@code Builder#subscribe(UpdateEvent.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnUpdate(Runnable runnable) {
            doOnUpdate(event -> runnable.run());
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

        /**
         * Equivalent to {@code Builder#subscribe(MouseHoverEnd.Start.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnHoverStart(Consumer<MouseHoverEvent.Start> consumer) {
            subscribe(MouseHoverEvent.Start.class, consumer);
            return self();
        }

        /**
         * Equivalent to {@code Builder#subscribe(MouseHoverEvent.Move.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnHoverMove(Consumer<MouseHoverEvent.Move> consumer) {
            subscribe(MouseHoverEvent.Move.class, consumer);
            return self();
        }

        /**
         * Equivalent to {@code Builder#subscribe(MouseHoverEvent.End.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnHoverEnd(Consumer<MouseHoverEvent.End> consumer) {
            subscribe(MouseHoverEvent.End.class, consumer);
            return self();
        }

        /**
         * Equivalent to {@code Builder#subscribe(TickEvent.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnTick(Consumer<TickEvent> consumer) {
            subscribe(TickEvent.class, consumer);
            return self();
        }

        /**
         * Equivalent to {@code Builder#subscribe(TickEvent.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnTick(Runnable runnable) {
            return doOnTick(event -> runnable.run());
        }

        /**
         * Equivalent to {@code Builder#subscribe(SizeAllocationEvent.class, consumer)}.
         * @see Builder#subscribe(Class, Consumer)
         */
        public B doOnSizeAllocate(Consumer<SizeAllocationEvent> consumer) {
            subscribe(SizeAllocationEvent.class, consumer);
            return self();
        }

        /**
         * Sets the given {@link Consumer} to accept the {@link MutableElement} created whenever
         * {@link ImmutableElement#asMutable()} is called. Useful for subscription to events where access to the
         * {@link MutableElement} is required or where direct modification of the {@link MutableElement} is required.
         */
        public B doOnCreate(Consumer<M> consumer) {
            onCreate.add(consumer);
            return self();
        }

        public abstract B self();

        public abstract T build();

    }

}
