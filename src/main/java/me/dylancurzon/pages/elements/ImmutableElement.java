package me.dylancurzon.pages.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.pages.InteractOptions;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.elements.mutable.MutableElement;
import me.dylancurzon.pages.elements.mutable.WrappingMutableElement;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public abstract class ImmutableElement {

    protected final Spacing margin;
    protected final Consumer<MutableElement> tickConsumer;
    protected final Function<MutableElement, WrappingMutableElement> mutator;
    protected final InteractOptions interactOptions;

    protected ImmutableElement(Builder builder) {
        if (builder.interactOptions == null) {
            interactOptions = InteractOptions.empty();
        } else {
            interactOptions = builder.interactOptions;
        }
        if (builder.margin == null) {
            margin = Spacing.ZERO;
        } else {
            margin = builder.margin;
        }
        tickConsumer = builder.tickConsumer;
        mutator = builder.mutator;
    }

    public MutableElement doMutate(MutableElement element) {
        if (mutator != null) {
            return mutator.apply(element);
        }
        return element;
    }

    @NotNull
    public abstract MutableElement asMutable();

    @NotNull
    public Spacing getMargin() {
        return margin;
    }

    public Consumer<MutableElement> getTickConsumer() {
        return tickConsumer;
    }

    @NotNull
    public InteractOptions getInteractOptions() {
        return interactOptions;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder> {

        protected Spacing margin;
        protected Consumer<MutableElement> tickConsumer;
        protected Function<MutableElement, WrappingMutableElement> mutator;
        protected InteractOptions interactOptions;

        @NotNull
        public B setMargin(Spacing margin) {
            this.margin = margin;
            return self();
        }

        @NotNull
        public B tick(Consumer<MutableElement> tickConsumer) {
            this.tickConsumer = tickConsumer;
            return self();
        }

        @NotNull
        public B mutate(Function<MutableElement, WrappingMutableElement> mutator) {
            this.mutator = mutator;
            return self();
        }

        @NotNull
        public B setInteractOptions(InteractOptions options) {
            interactOptions = options;
            return self();
        }

        @NotNull
        public abstract B self();

        @NotNull
        public abstract T build();

    }

}
