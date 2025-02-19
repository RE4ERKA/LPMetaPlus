package me.re4erka.lpmetaplus.placeholder.provider;

import me.re4erka.lpmetaplus.message.placeholder.Placeholders;

/**
 * Provides a {@link Placeholders} instance for text substitution.
 */
public interface PlaceholderProvider {

    Placeholders.Builder builderPlaceholders();

    default Placeholders toPlaceholders() {
        return builderPlaceholders().build();
    }
}
