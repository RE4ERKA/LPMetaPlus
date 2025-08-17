package me.re4erka.lpmetaplus.message.placeholder.provider;

import me.re4erka.lpmetaplus.message.placeholder.Placeholders;

/**
 * Provides a {@link Placeholders} instance for text substitution.
 */
public interface PlaceholderProvider {

    Placeholders.Builder placeholdersBuilder();

    default Placeholders toPlaceholders() {
        return placeholdersBuilder().build();
    }
}
