package me.re4erka.lpmetaplus.operation.context;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.util.Key;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public final class MetaOperationContext {

    private final CommandSender sender;
    private final String type;
    private final String target;

    private MetaOperationContext(@NotNull CommandSender sender, @NotNull String type, @NotNull String target) {
        this.sender = sender;
        this.type = type;
        this.target = target;
    }

    @NotNull
    public Key typeToKey() {
        return Key.of(type);
    }

    @NotNull
    public static MetaOperationContext of(@NotNull CommandSender sender, @NotNull String type, @NotNull String target) {
        return new MetaOperationContext(sender, type, target);
    }
}
