package me.re4erka.lpmetaplus.migration;

import lombok.RequiredArgsConstructor;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.migration.type.PlayerPointsMigrator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public enum MigrationType {
    PLAYER_POINTS(PlayerPointsMigrator::new);

    private final Function<LPMetaPlus, Migrator> initialize;

    public Migrator initialize(@NotNull LPMetaPlus lpMetaPlus) {
        return initialize.apply(lpMetaPlus);
    }
}
