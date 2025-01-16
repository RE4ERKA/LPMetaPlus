package me.re4erka.lpmetaplus.operation;

import me.re4erka.lpmetaplus.operation.context.MetaOperationContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MetaOperation {

    void execute(@NotNull MetaOperationContext context, boolean silent);

    default void execute(@NotNull MetaOperationContext context) {
        execute(context, false);
    }
}
