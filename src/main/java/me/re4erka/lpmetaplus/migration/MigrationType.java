package me.re4erka.lpmetaplus.migration;

import lombok.RequiredArgsConstructor;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.migration.type.PlayerPointsMigrator;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public enum MigrationType {
    PLAYER_POINTS(PlayerPointsMigrator::new, "PlayerPoints");

    private final Function<LPMetaPlus, Migrator> initializer;
    private final String pluginName;

    public boolean isDisabled() {
        return !Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public Migrator initialize(@NotNull LPMetaPlus lpMetaPlus) {
        return initializer.apply(lpMetaPlus);
    }
}
