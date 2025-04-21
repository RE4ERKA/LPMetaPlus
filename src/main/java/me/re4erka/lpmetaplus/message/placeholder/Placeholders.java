package me.re4erka.lpmetaplus.message.placeholder;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Placeholders {

    private final Set<Placeholder> placeholders;

    private Placeholders(@NotNull Set<Placeholder> placeholders) {
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
    public static Placeholders single(@NotNull String search, @NotNull String replacement) {
        return new Placeholders(Collections.unmodifiableSet(
                        Sets.newHashSet(Placeholder.of(search, replacement))));
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Set<Placeholder> placeholders = Sets.newHashSet();

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
        public Builder add(@NotNull String search, int replacement) {
            placeholders.add(Placeholder.of(search, Integer.toString(replacement)));
            return this;
        }

        @NotNull
        public Builder add(@NotNull String search, long replacement) {
            placeholders.add(Placeholder.of(search, Long.toString(replacement)));
            return this;
        }

        @NotNull
        public Placeholders build() {
            return new Placeholders(
                    Collections.unmodifiableSet(placeholders));
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
