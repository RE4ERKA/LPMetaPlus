package me.re4erka.lpmetaplus;

import com.google.common.collect.Lists;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.command.type.CustomCommand;
import me.re4erka.lpmetaplus.command.type.MainCommand;
import me.re4erka.lpmetaplus.configuration.ConfigurationMetas;
import me.re4erka.lpmetaplus.manager.type.GroupManager;
import me.re4erka.lpmetaplus.manager.type.MetaManager;
import me.re4erka.lpmetaplus.message.Message;
import me.re4erka.lpmetaplus.placeholder.MetaPlaceholder;
import me.re4erka.lpmetaplus.plugin.BasePlugin;
import me.re4erka.lpmetaplus.util.PluginEmulator;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.PermissionHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Getter
public final class LPMetaPlus extends BasePlugin<LPMetaPlus> {

    @Accessors(fluent = true)
    private Settings settings;
    @Accessors(fluent = true)
    private Messages messages;

    private GroupManager groupManager;

    @Accessors(fluent = true)
    private ConfigurationMetas metas;
    private MetaManager metaManager;

    @Override
    public void enable() {
        initialize("MetaManager", plugin -> this.metaManager = new MetaManager(plugin));
        initialize("GroupManager", plugin -> this.groupManager = new GroupManager(plugin));

        initialize("Emulation", plugin -> {
            if (settings.emulation().enabled()) {
                settings.emulation().applyTo().forEach(emulation -> {
                    if (Bukkit.getPluginManager().isPluginEnabled(emulation.getPluginName())) {
                        logError("The " + emulation.getPluginName() + " plugin cannot be emulated as it is already running on the server. "
                                + "Please disable the " + emulation.getPluginName() + " plugin to make the emulation work correctly.");
                        return;
                    }

                    if (settings.emulation().emulateLookupNames()) {
                        PluginEmulator.emulate(emulation.getPluginName());
                    }

                    emulation.load(plugin);
                    logInfo(emulation.getPluginName() + " plugin is successfully emulated.");
                });
            }
        });

        initialize("MetaPlaceholder", plugin -> {
            if (isSupportPlaceholderAPI()) {
                new MetaPlaceholder(plugin).register();
            } else {
                logNotFoundPlaceholderAPI();
            }
        });

        System.out.println("CoinsEngine isEnabled(): " + Bukkit.getPluginManager().isPluginEnabled("CoinsEngine"));
        System.out.println("PlayerPoints isEnabled(): " + Bukkit.getPluginManager().isPluginEnabled("PlayerPoints"));

        Bukkit.getScheduler().runTaskLater(this, () -> {
            settings.emulation().applyTo().forEach(emulation -> {
                PluginEmulator.emulate(emulation.getPluginName());
            });

            System.out.println("CoinsEngine isEnabled(): " + Bukkit.getPluginManager().isPluginEnabled("CoinsEngine"));
            System.out.println("PlayerPoints isEnabled(): " + Bukkit.getPluginManager().isPluginEnabled("PlayerPoints"));
        }, 3);

        groupManager.load(metas);
        metaManager.registerWarningEvents();
    }

    @Override
    protected void loadConfigurations() {
        final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .charset(StandardCharsets.UTF_8)
                .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                .addSerializer(Message.class, new Message.Serializer())
                .build();

        final Path dataFolder = getDataFolder().toPath();
        this.settings = YamlConfigurations.update(
                dataFolder.resolve("config.yml"),
                Settings.class, properties
        );
        this.messages = YamlConfigurations.update(
                dataFolder.resolve("messages.yml"),
                Messages.class, properties
        );
        this.metas = YamlConfigurations.update(
                dataFolder.resolve("metas.yml"),
                ConfigurationMetas.class, properties
        );
    }

    @Override
    protected void registerCommands() {
        final BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

        final Messages.Command messages = messages().command();
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS,
                (sender, context) -> messages.tooManyArguments().send(sender));
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS,
                (sender, context) -> messages.notEnoughArguments().send(sender));
        manager.registerMessage(MessageKey.INVALID_ARGUMENT,
                (sender, context) -> messages.invalidArguments().send(sender));
        manager.registerMessage(MessageKey.UNKNOWN_COMMAND,
                (sender, context) -> messages.unknownCommand().send(sender));
        manager.registerMessage(BukkitMessageKey.NO_PERMISSION,
                (sender, context) -> messages.noPermission().send(sender));

        manager.registerSuggestion(
                SuggestionKey.of("meta_types"),
                (sender, argument) -> metas.names()
        );
        manager.registerSuggestion(
                SuggestionKey.of("any_count"),
                (sender, argument) -> Lists.newArrayList("1", "10", "50", "100")
        );
        manager.registerSuggestion(
                SuggestionKey.of("loaded_user_names"),
                (sender, argument) -> LuckPermsProvider.get().getUserManager()
                        .getLoadedUsers().stream()
                        .map(PermissionHolder::getFriendlyName)
                        .collect(Collectors.toList())
        );

        manager.registerCommand(new MainCommand(this));
        metas.types().entrySet().stream()
                .filter(entry -> entry.getValue().isCommandEnabled())
                .forEach(entry -> manager.registerCommand(
                        new CustomCommand(this, entry.getKey(), entry.getValue())));
    }

    @NotNull
    @Override
    protected LPMetaPlus self() {
        return this;
    }
}
