package me.re4erka.lpmetaplus.api;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.session.MetaSession;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class LPMetaPlusAPI {

    private static LPMetaPlusAPI instance;

    private final LPMetaPlus plugin;

    @ApiStatus.Internal
    private LPMetaPlusAPI(@NotNull LPMetaPlus plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public MetaSession openSession(@NotNull Player player) {
        return plugin.getMetaManager().getUser(player);
    }

    @NotNull
    public CompletableFuture<MetaSession> openSession(@NotNull String username) {
        return plugin.getMetaManager().findUser(username);
    }

    public int getBalance(@NotNull Player player, @NotNull String key) {
        try (MetaSession session = openSession(player)) {
            return session.get(key);
        }
    }

    public void set(@NotNull Player player, @NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int balance,
                    boolean silent) {
        try (MetaSession session = openSession(player)) {
            session.edit(editor -> editor.set(key, balance), silent);
        }
    }

    public void set(@NotNull Player player, @NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int balance) {
        set(player, key, balance, false);
    }

    public void give(@NotNull Player player, @NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int balance,
                    boolean silent) {
        try (MetaSession session = openSession(player)) {
            session.edit(editor -> editor.give(key, balance), silent);
        }
    }

    public void give(@NotNull Player player, @NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int balance) {
        give(player, key, balance, false);
    }

    public void take(@NotNull Player player, @NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int balance,
                     boolean silent) {
        try (MetaSession session = openSession(player)) {
            session.edit(editor -> editor.give(key, balance), silent);
        }
    }

    public void take(@NotNull Player player, @NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int balance) {
        take(player, key, balance, false);
    }

    @NotNull
    public static LPMetaPlusAPI getInstance() throws NotRegisteredException {
        if (instance == null) {
            throw new NotRegisteredException();
        }

        return instance;
    }

    @ApiStatus.Internal
    public static void register(@NotNull LPMetaPlus plugin) {
        instance = new LPMetaPlusAPI(plugin);
    }

    @ApiStatus.Internal
    public static void unregister() {
        instance = null;
    }

    public static final class NotRegisteredException extends IllegalStateException {

        private static final String MESSAGE = "The LPMetaPlus API isn't loaded yet!\n" +
                "This could be because:\n" +
                "  a) LPMetaPlus has failed to enable successfully\n" +
                "  b) Your plugin isn't set to load after LPMetaPlus has " +
                "(Check if it set as a (soft)depend in plugin.yml or to load: BEFORE in paper-plugin.yml?)\n" +
                "  c) You are attempting to access LPMetaPlus on plugin construction/before your plugin has enabled.";

        NotRegisteredException() {
            super(MESSAGE);
        }
    }
}
