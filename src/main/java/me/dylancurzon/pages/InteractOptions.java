package me.dylancurzon.pages;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.elements.mutable.MutableElement;

import java.util.function.Consumer;

@Immutable
public class InteractOptions {

    private final boolean highlighting;
    private final Consumer<MutableElement> clickConsumer;

    public InteractOptions(boolean highlighting, Consumer<MutableElement> clickConsumer) {
        this.highlighting = highlighting;
        this.clickConsumer = clickConsumer;
    }

    public static InteractOptions empty() {
        return new InteractOptions(false, null);
    }

    public static InteractOptionsBuilder builder() {
        return new InteractOptionsBuilder();
    }

    public boolean shouldHighlight() {
        return highlighting;
    }

    public Consumer<MutableElement> getClickConsumer() {
        return clickConsumer;
    }

    public static class InteractOptionsBuilder {

        private boolean highlighting = false;
        private Consumer<MutableElement> clickConsumer;

        @NotNull
        public InteractOptionsBuilder setHighlighting(boolean value) {
            highlighting = value;
            return this;
        }

        @NotNull
        public InteractOptionsBuilder click(Consumer<MutableElement> consumer) {
            clickConsumer = consumer;
            return this;
        }

        @NotNull
        public InteractOptions build() {
            return new InteractOptions(highlighting, clickConsumer);
        }

    }

}
