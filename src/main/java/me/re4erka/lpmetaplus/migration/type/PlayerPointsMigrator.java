package me.re4erka.lpmetaplus.migration.type;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.zaxxer.hikari.HikariDataSource;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.migration.Migrator;
import me.re4erka.lpmetaplus.migration.data.MigrationData;
import me.re4erka.lpmetaplus.migration.result.MigrationResult;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlayerPointsMigrator extends Migrator {

    public PlayerPointsMigrator(@NotNull LPMetaPlus lpMetaPlus) {
        super("PlayerPoints", lpMetaPlus);
    }

    @Override
    public CompletableFuture<MigrationResult> migrate(@NotNull DatabaseType type) {
        return CompletableFuture.supplyAsync(() -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final String jdbcUrl = type == DatabaseType.SQLITE
                    ? "jdbc:sqlite:" + getDatabaseFilePath(name.toLowerCase(Locale.ROOT))
                    : "jdbc:mysql://" + credentials.host() + ":" + credentials.port() + "/" + credentials.database();

            try (HikariDataSource connectionPool = new HikariDataSource()) {
                connectionPool.setJdbcUrl(jdbcUrl);
                connectionPool.setUsername(credentials.username());
                connectionPool.setPassword(credentials.password());
                connectionPool.setPoolName("lpmetaplus_migrator_pool");

                final Set<MigrationData> migrationDataSet = Sets.newHashSet();
                try (Connection connection = connectionPool.getConnection()) {
                    final String query = "SELECT p.uuid, p.points, u.username FROM `playerpoints_points` p INNER "
                            + "JOIN `playerpoints_username_cache` u ON p.uuid = u.uuid;";

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        try (ResultSet result = statement.executeQuery()) {

                            int playersMigrated = 0;
                            while (result.next()) {
                                final String uuid = result.getString("uuid");
                                final String username = result.getString("username");
                                final int points = result.getInt("points");

                                final MigrationData data = MigrationData.of(
                                        UUID.fromString(uuid), username, points
                                );

                                migrationDataSet.add(data);

                                playersMigrated++;
                                if (playersMigrated % 100 == 0) {
                                    lpMetaPlus.logInfo("Downloaded " + name
                                            + " data for " + playersMigrated + " players...");
                                }
                            }

                            migrateAll(migrationDataSet);
                            return MigrationResult.of(
                                    migrationDataSet.size(),
                                    stopwatch.elapsed(TimeUnit.MILLISECONDS)
                            );
                        }
                    }
                }
            } catch (Throwable exception) {
                lpMetaPlus.logError("An error occurred when migrating from " + name + " with " + type.name() + ". "
                        + "The connection credentials may have been entered incorrectly.", exception);
                return MigrationResult.failed(stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        });
    }
}
