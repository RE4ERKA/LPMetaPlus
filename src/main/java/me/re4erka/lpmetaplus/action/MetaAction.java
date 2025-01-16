package me.re4erka.lpmetaplus.action;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.util.Key;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Getter
@Accessors(fluent = true)
@Builder
public class MetaAction {

    private final Type type;

    private final Key key;
    private final int count;

    private final Instant timestamp = Instant.now();

    private static final CharSequence SPACE = " ";

    @NotNull
    public String toDescription() {
        return String.join(SPACE, type.action, key.toLowerCase(), Integer.toUnsignedString(count));
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        SET("set"),
        GIVE("give"),
        TAKE("take");

        @Accessors(fluent = true)
        private final String action;
    }
}
