package me.re4erka.lpmetaplus.message.placeholder;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Placeholders {

    private final List<Placeholder> placeholders;

    private Placeholders(@NotNull List<Placeholder> placeholders) {
        this.placeholders = placeholders;
    }

    @NotNull
    public String process(@NotNull String text) {
        String processedText = text;
        for (Placeholder placeholder : placeholders) {
            processedText = processedText.replace(placeholder.search(), placeholder.replacement());
        }

        return processedText;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Placeholder> placeholders = new ArrayList<>();

        @NotNull
        public Builder add(@NotNull String search, @Nullable String replacement) {
            placeholders.add(Placeholder.of(search, notNull(replacement)));
            return this;
        }

        @NotNull
        public Builder add(@NotNull String search, @Nullable Character replacement) {
            placeholders.add(Placeholder.of(search, notNullToString(replacement)));
            return this;
        }

        @NotNull
        public Placeholders build() {
            return new Placeholders(
                    Collections.unmodifiableList(placeholders)
            );
        }

        @NotNull
        private String notNull(@Nullable String value) {
            return value == null ? StringUtils.EMPTY : value;
        }

        @NotNull
        private String notNullToString(@Nullable Character value) {
            return value == null ? StringUtils.EMPTY : Character.toString(value);
        }
    }
}
