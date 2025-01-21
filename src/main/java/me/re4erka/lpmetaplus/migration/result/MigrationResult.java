package me.re4erka.lpmetaplus.migration.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.message.placeholder.Placeholders;
import me.re4erka.lpmetaplus.migration.MigrationType;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class MigrationResult {

    private final int playersMigrated;
    private final long tookToMillis;

    public boolean isFailed() {
        return playersMigrated == 0;
    }

    public Placeholders toPlaceholders(@NotNull MigrationType type) {
        return Placeholders.builder()
                .add("count", playersMigrated)
                .add("took", tookToMillis)
                .add("name", type.name())
                .build();
    }

    public static MigrationResult failed(long millis) {
        return new MigrationResult(0, millis);
    }
}
