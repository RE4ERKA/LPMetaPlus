package me.re4erka.lpmetaplus.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UtilityClass
public final class OfflineUUID {

    private static final String PREFIX = "OfflinePlayer:";

    @NotNull
    public UUID fromName(@NotNull String name) {
        final String prefixedName = PREFIX + name;
        return UUID.nameUUIDFromBytes(
                prefixedName.getBytes(StandardCharsets.UTF_8));
    }
}
