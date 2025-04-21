package me.re4erka.lpmetaplus.operation.factory;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.operation.type.EditableMetaOperation;
import me.re4erka.lpmetaplus.operation.type.ReturnedMetaOperation;
import me.re4erka.lpmetaplus.session.MetaSession;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class MetaOperationFactory {

    private MetaOperationFactory() {
        throw new UnsupportedOperationException("This is a factory class and cannot be instantiated");
    }

    public static ReturnedMetaOperation createReturned(@NotNull LPMetaPlus lpMetaPlus,
                                                       @NotNull BiConsumer<CustomMeta, Integer> thenAction) {
        return new ReturnedMetaOperation(lpMetaPlus, thenAction);
    }

    public static EditableMetaOperation createEditable(@NotNull LPMetaPlus lpMetaPlus,
                                                       @NotNull BiConsumer<String, MetaSession.Editor> editable,
                                                       @NotNull Consumer<CustomMeta> thenAction) {
        return new EditableMetaOperation(lpMetaPlus, editable, thenAction);
    }
}
