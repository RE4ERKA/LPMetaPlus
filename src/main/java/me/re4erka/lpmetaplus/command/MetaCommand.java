package me.re4erka.lpmetaplus.command;

import dev.triumphteam.cmd.core.BaseCommand;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.Messages;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.message.placeholder.Placeholders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MetaCommand extends BaseCommand {

    protected final LPMetaPlus lpMetaPlus;

    protected MetaCommand(@NotNull LPMetaPlus lpMetaPlus) {
        this.lpMetaPlus = lpMetaPlus;
    }

    protected MetaCommand(@NotNull LPMetaPlus lpMetaPlus, @NotNull String command, @NotNull List<String> alias) {
        super(command, alias);
        this.lpMetaPlus = lpMetaPlus;
    }

    @NotNull
    protected Messages.Command commandMessages() {
        return lpMetaPlus.messages().command();
    }

    @NotNull
    protected Messages.Meta metaMessages() {
        return lpMetaPlus.messages().meta();
    }

    @NotNull
    protected Placeholders buildPlaceholders(@NotNull String target, @NotNull CustomMeta meta, int count) {
        return builderPlaceholders(meta, count)
                .add("target", target)
                .build();
    }

    @NotNull
    protected Placeholders buildPlaceholders(@NotNull CustomMeta meta, int balance) {
        return builderPlaceholders(meta, balance).build();
    }

    @NotNull
    private Placeholders.Builder builderPlaceholders(@NotNull CustomMeta meta, int balance) {
        return Placeholders.builder()
                .add("display_name", meta.displayName())
                .add("symbol", meta.symbol())
                .add("balance", Integer.toString(balance));
    }
}
