package me.re4erka.lpmetaplus.message.placeholder;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public final class Placeholder {

    private final String search;
    private final String replacement;

    private Placeholder(@NotNull String search, @NotNull String replacement) {
        this.search = convert(search);
        this.replacement = replacement;
    }

    @NotNull
    private String convert(@NotNull String raw) {
        return '%' + raw + '%';
    }

    @NotNull
    public static Placeholder of(@NotNull String search, @NotNull String replacement) {
        return new Placeholder(search, replacement);
    }
}
