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
    private static final String META_PREFIX = "meta";

    @NotNull
    public String toDescription() {
        return String.join(SPACE, META_PREFIX, type.action, key.toString(), Integer.toUnsignedString(count));
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
