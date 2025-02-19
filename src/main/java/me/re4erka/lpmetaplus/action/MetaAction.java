package me.re4erka.lpmetaplus.action;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.util.Key;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.StringJoiner;

@Getter
@Accessors(fluent = true)
@Builder
public class MetaAction {

    private final Type type;

    private final Key key;
    private final Integer count;

    private final Instant timestamp = Instant.now();

    private static final CharSequence SPACE = " ";
    private static final String META_PREFIX = "meta";

    @NotNull
    public String toDescription() {
        final StringJoiner joiner = new StringJoiner(SPACE);
        joiner.add(META_PREFIX)
                .add(type.action);

        return count == null
                ? joiner.toString()
                : joiner.add(countToString()).toString();
    }

    private String countToString() {
        return Integer.toString(count);
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        SET("set"),
        GIVE("give"),
        TAKE("take"),
        RESET("reset");

        @Accessors(fluent = true)
        private final String action;
    }
}
