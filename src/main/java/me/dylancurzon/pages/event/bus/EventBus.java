package me.dylancurzon.pages.event.bus;

import java.util.function.Consumer;

public interface EventBus {

    void post(Object event);

    <T> void subscribe(EventListener<T> listener);

    default <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        subscribe(new EventListener<>(eventType, listener));
    }

    void removeSubscription(Consumer listener);

}
