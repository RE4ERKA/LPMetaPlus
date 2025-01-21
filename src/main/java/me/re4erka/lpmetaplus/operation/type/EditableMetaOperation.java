package me.re4erka.lpmetaplus.operation.type;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.operation.AbstractMetaOperation;
import me.re4erka.lpmetaplus.operation.context.MetaOperationContext;
import me.re4erka.lpmetaplus.session.MetaSession;
import me.re4erka.lpmetaplus.util.Key;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EditableMetaOperation extends AbstractMetaOperation {

    private final BiConsumer<Key, MetaSession.Editor> editable;
    private final Consumer<CustomMeta> thenAction;

    public EditableMetaOperation(@NotNull LPMetaPlus lpMetaPlus,
                                 @NotNull BiConsumer<Key, MetaSession.Editor> editable,
                                 @NotNull Consumer<CustomMeta> thenAction) {
        super(lpMetaPlus);
        this.editable = editable;
        this.thenAction = thenAction;
    }

    @Override
    public void execute(@NotNull MetaOperationContext context, boolean silent) {
        ifMetaPresent(context, meta -> lpMetaPlus.getMetaManager().findUser(context.target())
                .thenAccept(session -> tryRunOperation(session, () -> session.edit(
                        editor -> editable.accept(context.typeToKey(), editor), silent)))
                .thenRun(() -> runIfNotSilent(() -> thenAction.accept(meta), silent))
                .exceptionally(throwable -> throwException(context, throwable)));
    }
}
