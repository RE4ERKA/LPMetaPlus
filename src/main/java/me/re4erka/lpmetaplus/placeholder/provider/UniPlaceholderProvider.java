package me.re4erka.lpmetaplus.placeholder.provider;

import me.re4erka.lpmetaplus.message.placeholder.Placeholders;

/**
 * Converts an object of type {@code A} into a {@link Placeholders} instance for text substitution.
 */
public interface UniPlaceholderProvider<A> {

    Placeholders.Builder builderPlaceholders(A a);

    default Placeholders toPlaceholders(A a) {
        return builderPlaceholders(a).build();
    }
}
