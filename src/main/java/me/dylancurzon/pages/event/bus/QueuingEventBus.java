package me.dylancurzon.pages.event.bus;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class QueuingEventBus implements EventBus {

    private final Set<EventListener> listeners = new HashSet<>();
    private final Queue<Object> eventQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void post(Object event) {
        // By default, consume this event immediately
        post(event, true);
    }

    /**
     * Posts the given event, to be eventually consumed by each subscribed ({@link this#subscribe(EventListener)})
     * {@link EventListener} which accepts this event class.
     * @param event The event to post
     * @param immediate If {@code true}, then the {@link EventListener}s will consume the given event on the current
     *                  Thread, immediately, such that when this method returns the event has been completely consumed
     *                  by this {@link QueuingEventBus}. If {@code false}, then the event is queued for consumption on
     *                  the next call of {@link this#consume()} and this method will return immediately.
     */
    public void post(Object event, boolean immediate) {
        if (immediate) {
            consumeEvent(event);
        } else {
            eventQueue.add(event);
        }
    }

    @Override
    public <T> void subscribe(EventListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeSubscription(Consumer consumer) {
        listeners.removeIf(listener -> listener.getConsumer().equals(consumer));
    }

    /**
     * Consumes the events that this {@link QueuingEventBus} has had posted since the last call to this method by
     * forwarding them to all {@link EventListener}s which accept the class of the event.
     */
    protected void consume() {
        while (!eventQueue.isEmpty()) {
            Object event = eventQueue.remove();
            consumeEvent(event);
        }
    }

    @SuppressWarnings("unchecked")
    private void consumeEvent(Object event) {
        listeners.stream()
            .filter(listener -> listener.getEventClass().isAssignableFrom(event.getClass()))
            .forEach(listener -> listener.getConsumer().accept(event));
    }

}
