package me.dylancurzon.pages.event.bus;

import java.util.function.Consumer;

public class EventListener<T> {

    private final Class<T> eventClass;
    private final Consumer<T> consumer;

    public EventListener(Class<T> eventClass, Consumer<T> consumer) {
        this.eventClass = eventClass;
        this.consumer = consumer;
    }

    public Class<T> getEventClass() {
        return eventClass;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

}
