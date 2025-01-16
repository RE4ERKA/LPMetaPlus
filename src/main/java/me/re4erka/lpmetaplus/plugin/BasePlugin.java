package me.re4erka.lpmetaplus.plugin;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.api.LPMetaPlusAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class BasePlugin<P extends LPMetaPlus> extends JavaPlugin {

    @Override
    public void onEnable() {
        throwIfNotRelocated();
        loadConfigurations();
        enable();
        registerCommands();
        LPMetaPlusAPI.register(self());
    }

    @Override
    public void onDisable() {
        LPMetaPlusAPI.unregister();
    }

    public void reload() {
        loadConfigurations();
    }

    public abstract void enable();

    public void logInfo(@NotNull String message) {
        getLogger().log(Level.INFO, message);
    }

    public void logError(@NotNull String message) {
        getLogger().log(Level.SEVERE, message);
    }

    public void logError(@NotNull String message, @NotNull Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
    }

    protected abstract void loadConfigurations();
    protected abstract void registerCommands();

    protected abstract P self();

    protected void initialize(@NotNull String name, @NotNull Consumer<P> initialize) {
        getLogger().log(Level.INFO, "Initializing {0}...", name);
        try {
            initialize.accept(self());
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "An error occurred while initializing {0}!", name);
            exception.fillInStackTrace();
        }
    }

    protected boolean isSupportPlaceholderAPI() {
        return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    protected void logNotFoundPlaceholderAPI() {
        logInfo("PlaceholderAPI was not found. MetaPlaceholder class is ignored.");
    }

    private void throwIfNotRelocated() {
        try {
            Class.forName("me.re4erka.lpmetaplus.libraries.command.bukkit.BukkitCommandManager");
            Class.forName("me.re4erka.lpmetaplus.libraries.configuration.YamlConfigurations");
        } catch (ClassNotFoundException exception) {
            logError("Please relocate dependencies correctly or buy the plugin because it won't have this error :)");
            throw new RuntimeException(exception);
        }
    }
}
