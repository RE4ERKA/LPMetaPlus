package me.re4erka.lpmetaplus.migration;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.Settings;
import me.re4erka.lpmetaplus.manager.type.MetaManager;
import me.re4erka.lpmetaplus.migration.data.MigrationData;
import me.re4erka.lpmetaplus.migration.result.MigrationResult;
import me.re4erka.lpmetaplus.session.MetaSession;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class Migrator {

    protected final LPMetaPlus lpMetaPlus;

    protected final String name;
    protected final Plugin plugin;

    protected final String defaultType;
    protected final Settings.Migration.Credentials credentials;

    protected static final String MIGRATION_POOL_NAME = "lpmetaplus_migration_pool";

    protected Migrator(@NotNull String name,
                       @NotNull LPMetaPlus lpMetaPlus) {
        this.lpMetaPlus = lpMetaPlus;

        this.name = name;
        this.plugin = Bukkit.getPluginManager().getPlugin(name);
        throwIfPluginIsNull(plugin);

        final Settings.Migration migration = lpMetaPlus.settings().migration();
        throwIfTypeNotFound(migration.defaultType());
        this.defaultType = migration.defaultType();
        this.credentials = migration.credentials();
    }

    public abstract CompletableFuture<MigrationResult> migrate(@NotNull DatabaseType type);

    protected Path filePath(@NotNull String fileName) {
        return plugin.getDataFolder().toPath().resolve(fileName + ".db");
    }

    protected String url(@NotNull String host, int port, @NotNull String database) {
        return host + ':' + port + '/' + database;
    }

    @Blocking
    protected void migrateAll(@NotNull Set<MigrationData> dataList) {
        final MetaManager metaManager = lpMetaPlus.getMetaManager();
        for (MigrationData data : dataList) {
            metaManager.findUser(data.uuid(), data.username())
                    .thenAccept(session -> {
                        try (MetaSession ignored = session) {
                            session.edit(editor -> editor.set(defaultType, data.balance()), true);
                        }
                    }).join();
        }
    }

    private void throwIfPluginIsNull(@Nullable Plugin plugin) {
        if (plugin == null) {
            lpMetaPlus.logError(name + " plugin was not found! It may be disabled or not installed, "
                    + "but it is required to migrate.");
            throw new NullPointerException("The plugin must not be null");
        }
    }

    private void throwIfTypeNotFound(@NotNull String type) {
        if (!lpMetaPlus.metas().contains(type)) {
            throw new IllegalArgumentException("The type of meta was not found");
        }
    }

    public enum DatabaseType {
        SQLITE, MYSQL
    }
}
