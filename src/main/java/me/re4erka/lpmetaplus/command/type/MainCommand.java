package me.re4erka.lpmetaplus.command.type;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.*;
import dev.triumphteam.cmd.core.flag.Flags;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.command.MetaCommand;
import me.re4erka.lpmetaplus.operation.MetaOperation;
import me.re4erka.lpmetaplus.operation.context.MetaOperationContext;
import me.re4erka.lpmetaplus.operation.factory.MetaOperationFactory;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Command(value = "lpmetaplus", alias = {"meta", "metas"})
public class MainCommand extends MetaCommand {

    public MainCommand(@NotNull LPMetaPlus lpMetaPlus) {
        super(lpMetaPlus);
    }

    @Default
    @Permission("lpmetaplus.command.help")
    public void onDefault(@NotNull CommandSender sender) {
        onHelp(sender);
    }

    @SubCommand("get")
    @Permission("lpmetaplus.command.get")
    public void onGet(@NotNull CommandSender sender, @NotNull @Suggestion("meta_types") String type,
                      @NotNull @Suggestion("loaded_user_names") String target) {
        final MetaOperationContext context = MetaOperationContext.of(sender, type, target);
        final MetaOperation operation = MetaOperationFactory.createReturned(lpMetaPlus,
                (meta, count) -> metaMessages().get().send(sender, buildPlaceholders(target, meta, count)));

        operation.execute(context);
    }

    @SubCommand("set")
    @Permission("lpmetaplus.command.set")
    @CommandFlags({@Flag(flag = "silent")})
    public void onSet(@NotNull CommandSender sender, @NotNull @Suggestion("meta_types") String type,
                      @Suggestion("any_count") int count, @NotNull @Suggestion("loaded_user_names") String target,
                      @NotNull Flags flags) {
        final MetaOperationContext context = MetaOperationContext.of(sender, type, target);
        final MetaOperation operation = MetaOperationFactory.createEditable(lpMetaPlus,
                (key, editor) -> editor.set(key, count),
                (meta) -> metaMessages().set().send(sender, buildPlaceholders(target, meta, count)));

        operation.execute(context, flags.hasFlag("silent"));
    }

    @SubCommand("give")
    @Permission("lpmetaplus.command.give")
    @CommandFlags({@Flag(flag = "silent")})
    public void onGive(@NotNull CommandSender sender, @NotNull @Suggestion("meta_types") String type,
                       @Suggestion("any_count") int count, @NotNull @Suggestion("loaded_user_names") String target,
                       @NotNull Flags flags) {
        final MetaOperationContext context = MetaOperationContext.of(sender, type, target);
        final MetaOperation operation = MetaOperationFactory.createEditable(lpMetaPlus,
                (key, editor) -> editor.give(key, count),
                (meta) -> metaMessages().given().send(sender, buildPlaceholders(target, meta, count)));

        operation.execute(context, flags.hasFlag("silent"));
    }

    @SubCommand("take")
    @Permission("lpmetaplus.command.take")
    @CommandFlags({@Flag(flag = "silent")})
    public void onTake(@NotNull CommandSender sender, @NotNull @Suggestion("meta_types") String type,
                       @Suggestion("any_count") int count, @NotNull @Suggestion("loaded_user_names") String target,
                       @NotNull Flags flags) {
        final MetaOperationContext context = MetaOperationContext.of(sender, type, target);
        final MetaOperation operation = MetaOperationFactory.createEditable(lpMetaPlus,
                (key, editor) -> editor.take(key, count),
                (meta) -> metaMessages().taken().send(sender, buildPlaceholders(target, meta, count)));

        operation.execute(context, flags.hasFlag("silent"));
    }

    @SubCommand("reload")
    @Permission("lpmetaplus.command.reload")
    public void onReload(@NotNull CommandSender sender) {
        lpMetaPlus.reload();
        commandMessages().reloaded().send(sender);
    }

    @SubCommand("help")
    @Permission("lpmetaplus.command.help")
    public void onHelp(@NotNull CommandSender sender) {
        commandMessages().help()
                .forEach(line -> line.send(sender));
    }
}
