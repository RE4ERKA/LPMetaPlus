package me.re4erka.lpmetaplus.operation.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public final class MetaOperationContext {

    private final CommandSender sender;
    private final String type;
    private final String target;
}
