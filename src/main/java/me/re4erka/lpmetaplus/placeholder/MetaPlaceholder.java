package me.re4erka.lpmetaplus.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.manager.type.MetaManager;
import me.re4erka.lpmetaplus.session.MetaSession;
import me.re4erka.lpmetaplus.util.Formatter;
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
    public boolean persist() {
        return true;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String rawParams) {
        if (player == null || !player.isOnline()) {
            return PLAYER_NOT_ONLINE;
        }

        final String[] params = convert(rawParams);
        final String type = params[0].toUpperCase(Locale.ROOT);

        if (!lpMetaPlus.metas().contains(type)) {
            return META_NOT_FOUND;
        }

        if (params.length == 1) {
            try (MetaSession session = metaManager.getUser(player)) {
                return session.getAsString(type);
            }
        }

        final CustomMeta meta = lpMetaPlus.metas().getIfPresent(type);
        switch (params.length) {
            case 2: {
                if (params[1].equals("formatted")) {
                    try (MetaSession session = metaManager.getUser(player)) {
                        return lpMetaPlus.getCurrencyFormatter()
                                .format(session.get(type), meta.symbol());
                    }
                } else if (params[1].equals("symbol")) {
                    return meta.symbol() == null ? EMPTY_SYMBOL : Character.toString(meta.symbol());
                }
            }
            case 3: {
                if (params[1].equals("with") && params[2].equals("symbol")) {
                    try (MetaSession session = metaManager.getUser(player)) {
                        return session.getAsString(type) + meta.symbol();
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
