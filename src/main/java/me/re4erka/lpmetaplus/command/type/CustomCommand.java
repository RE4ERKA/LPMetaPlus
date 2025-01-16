package me.re4erka.lpmetaplus.command.type;

import dev.triumphteam.cmd.core.annotation.Default;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.command.MetaCommand;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.manager.type.MetaManager;
import me.re4erka.lpmetaplus.util.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CustomCommand extends MetaCommand {

    private final MetaManager metaManager;

    private final Key key;
    private final CustomMeta meta;

    private final String permission;

    public CustomCommand(@NotNull LPMetaPlus lpMetaPlus,
                         @NotNull String type, @NotNull CustomMeta meta) {
        super(lpMetaPlus, type, meta.command().alias());
        this.metaManager = lpMetaPlus.getMetaManager();

        this.key = Key.of(type);
        this.meta = meta;

        this.permission = meta.command().permission();
    }

    @Default
    public void onDefault(@NotNull Player player) {
        if (permission != null && !player.hasPermission(permission)) {
            commandMessages().noPermission().send(player);
            return;
        }

        final int count = metaManager.getUser(player).get(key);
        meta.command().message().send(player, buildPlaceholders(meta, count));
    }
}
