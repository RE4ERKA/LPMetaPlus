package me.re4erka.lpmetaplus.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.manager.type.MetaManager;
import me.re4erka.lpmetaplus.session.MetaSession;
import me.re4erka.lpmetaplus.util.Formatter;
import me.re4erka.lpmetaplus.util.Key;
import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MetaPlaceholder extends PlaceholderExpansion {

    private final LPMetaPlus lpMetaPlus;

    private final MetaManager metaManager;

    private static final String SEPARATOR = "_";

    private static final String META_NOT_FOUND = "meta not found";
    private static final String PLAYER_NOT_ONLINE = "player not online";
    private static final String PLACEHOLDER_NOT_FOUND = "placeholder not found";

    private static final String EMPTY_DISPLAY_NAME = "empty display name";
    private static final String EMPTY_SYMBOL = "empty symbol";

    public MetaPlaceholder(@NotNull LPMetaPlus lpMetaPlus) {
        this.lpMetaPlus = lpMetaPlus;

        this.metaManager = lpMetaPlus.getMetaManager();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "meta";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "RE4ERKA";
    }

    @NotNull
    @Override
    public String getVersion() {
        return lpMetaPlus.getDescription().getVersion();
    }

    @Override
    @Nullable
    public String onRequest(@NotNull OfflinePlayer player, @NotNull String rawParams) {
        if (!player.isOnline()) {
            return PLAYER_NOT_ONLINE;
        }

        final String[] params = convert(rawParams);
        final Key key = Key.of(params[0]);

        if (!lpMetaPlus.metas().contains(key)) {
            return META_NOT_FOUND;
        }

        if (params.length == 1) {
            try (MetaSession session = metaManager.getUser(player)) {
                return session.getAsString(key);
            }
        }

        final CustomMeta meta = lpMetaPlus.metas().type(key);
        switch (params.length) {
            case 2: {
                if (params[1].equals("symbol")) {
                    return meta.symbol() == null ? EMPTY_SYMBOL : Character.toString(meta.symbol());
                }
            }
            case 3: {
                if (params[1].equals("with") && params[2].equals("symbol")) {
                    try (MetaSession session = metaManager.getUser(player)) {
                        return session.getAsString(key) + meta.symbol();
                    }
                } else if (params[1].equals("display") && params[2].equals("name")) {
                    return meta.displayName() == null ? EMPTY_DISPLAY_NAME : Formatter.format(meta.displayName());
                } else if (params[1].equals("default") && params[2].equals("value")) {
                    return meta.defaultValueToString();
                }
            }
        }

        return PLACEHOLDER_NOT_FOUND;
    }

    @NotNull
    private String[] convert(@NotNull String params) {
        params = params.toLowerCase(Locale.ROOT);
        return StringUtils.split(params, SEPARATOR, 3);
    }
}
