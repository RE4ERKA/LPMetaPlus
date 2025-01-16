package me.re4erka.lpmetaplus.operation.type;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.operation.AbstractMetaOperation;
import me.re4erka.lpmetaplus.operation.context.MetaOperationContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ReturnedMetaOperation extends AbstractMetaOperation {

    private final BiConsumer<CustomMeta, Integer> thenAction;

    public ReturnedMetaOperation(@NotNull LPMetaPlus lpMetaPlus,
                                 @NotNull BiConsumer<CustomMeta, Integer> thenAction) {
        super(lpMetaPlus);
        this.thenAction = thenAction;
    }

    @Override
    public void execute(@NotNull MetaOperationContext context, boolean silent) {
        ifMetaPresent(context, meta -> lpMetaPlus.getMetaManager().findUser(context.target())
                .thenApply(session -> tryRunOperationAndReturn(session, () -> session.get(context.typeToKey())))
                .thenAccept(count -> runIfNotSilent(() -> thenAction.accept(meta, count), silent))
                .exceptionally(throwable -> throwException(context, throwable)));
    }
}
