package me.re4erka.lpmetaplus.migration.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.message.placeholder.Placeholders;
import me.re4erka.lpmetaplus.migration.MigrationType;
import me.re4erka.lpmetaplus.placeholder.provider.UniPlaceholderProvider;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "success")
public class MigrationResult implements UniPlaceholderProvider<MigrationType> {

    private final int playersMigrated;
    private final long tookToMillis;

    public boolean isError() {
        return playersMigrated == 0;
    }

    @Override
    public Placeholders.Builder placeholdersBuilder(@NotNull MigrationType type) {
        return Placeholders.builder()
                .add("count", playersMigrated)
                .add("took", tookToMillis)
                .add("name", type.name());
    }

    public static MigrationResult error(long millis) {
        return new MigrationResult(0, millis);
    }
}
