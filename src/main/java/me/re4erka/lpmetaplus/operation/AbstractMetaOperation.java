package me.re4erka.lpmetaplus.operation;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.operation.context.MetaOperationContext;
import me.re4erka.lpmetaplus.session.MetaSession;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractMetaOperation implements MetaOperation {

    protected final LPMetaPlus lpMetaPlus;

    protected AbstractMetaOperation(@NotNull LPMetaPlus lpMetaPlus) {
        this.lpMetaPlus = lpMetaPlus;
    }

    protected void ifMetaPresent(@NotNull MetaOperationContext context, @NotNull Consumer<CustomMeta> action) {
        final Optional<CustomMeta> optionalMeta = lpMetaPlus.metas().type(context.type());
        if (!optionalMeta.isPresent()) {
            lpMetaPlus.messages().meta().notFound().send(context.sender());
            return;
        }

        action.accept(optionalMeta.get());
    }

    protected void tryRunOperation(@NotNull MetaSession session, @NotNull Runnable action) {
        try (MetaSession ignored = session) {
            action.run();
        } catch (Exception exception) {
            if (session.isLookup()) {
                logCleanupError();
            }
            throw exception;
        }
    }

    protected int tryRunOperationAndReturn(@NotNull MetaSession session, @NotNull Supplier<Integer> returnable) {
        try (MetaSession ignored = session) {
            return returnable.get();
        } catch (Exception exception) {
            if (session.isLookup()) {
                logCleanupError();
            }
            throw exception;
        }
    }

    protected void runIfNotSilent(@NotNull Runnable action, boolean silent) {
        if (!silent) {
            action.run();
        }
    }

    @Nullable
    protected Void throwException(@NotNull MetaOperationContext context, @NotNull Throwable throwable) {
        logError(throwable);
        sendError(context.sender());
        return null;
    }

    private void logCleanupError() {
        lpMetaPlus.logError("There was an error in the metadata operations session, "
                + "the user obtained by lookup was cleaned up to prevent memory leaks.");
    }

    private void sendError(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.COLOR_CHAR + ChatColor.RED.toString()
                + "При выполнении операции с мета-данной произошла ошибка. "
                + "Проверьте консоль, чтобы узнать подробнее.");
    }

    private void logError(@NotNull Throwable throwable) {
        lpMetaPlus.logError("An error occurred when the operation was running with metadata", throwable);
    }
}
