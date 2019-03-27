package me.dylancurzon.pages.event.bus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SimpleEventBus implements EventBus {

    private final Set<EventListener> listeners = new HashSet<>();

    @SuppressWarnings("unchecked")
    @Override
    public void post(Object event) {
        listeners.stream()
            .filter(listener -> listener.getEventClass().isAssignableFrom(event.getClass()))
            .forEach(listener -> listener.getConsumer().accept(event));
    }

    @Override
    public <T> void subscribe(EventListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeSubscription(Consumer consumer) {
        listeners.removeIf(listener -> listener.getConsumer().equals(consumer));
    }

}
