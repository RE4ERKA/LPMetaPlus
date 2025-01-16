package me.re4erka.lpmetaplus.manager.type;

import com.google.common.base.Stopwatch;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.configuration.ConfigurationMetas;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.manager.LuckPermsProviderManager;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GroupManager extends LuckPermsProviderManager {

    public static final String CONTEXT_KEY = "meta-plus";

    public GroupManager(@NotNull LPMetaPlus lpMetaPlus) {
        super(lpMetaPlus);
    }

    public void load(@NotNull ConfigurationMetas metas) {
        lpMetaPlus.logInfo("Setting default metadata for group 'default'...");
        final Stopwatch stopwatch = Stopwatch.createStarted();

        groupManager.loadGroup("default")
                .thenAccept(optionalGroup -> {
                    final Group group = optionalGroup.orElseThrow(this::throwIfGroupNotFound);
                    loadDefaultGroupValues(group, metas.types());
                    clearRemovedMetadata(group, metas.names());

                    groupManager.saveGroup(group);
                }).join();

        logResult(metas.types(), stopwatch);
    }

    private void loadDefaultGroupValues(@NotNull Group group, @NotNull Map<String, CustomMeta> metaMap) {
        for (Map.Entry<String, CustomMeta> entry : metaMap.entrySet()) {
            final CustomMeta meta = entry.getValue();

            final ImmutableContextSet.Builder contextBuilder = newContextBuilder();
            meta.defaultContexts().forEach(contextBuilder::add);

            final MetaNode node = MetaNode.builder()
                    .key(entry.getKey().toLowerCase(Locale.ROOT))
                    .value(meta.defaultValueToString())
                    .context(contextBuilder.build())
                    .build();

            group.data().add(node);
        }
    }

    private void clearRemovedMetadata(@NotNull Group group, @NotNull List<String> names) {
        final Set<MetaNode> nodes = group.getNodes(NodeType.META).stream()
                .filter(node -> node.getContexts().contains(CONTEXT_KEY, Boolean.TRUE.toString()))
                .filter(node -> !names.contains(node.getMetaKey().toUpperCase(Locale.ROOT)))
                .collect(Collectors.toSet());

        if (!nodes.isEmpty()) {
            nodes.forEach(node -> group.data().remove(node));
            lpMetaPlus.getLogger().log(Level.INFO, "Removed metadata was found, cleared {0}", nodes.size());
        }
    }

    @NotNull
    private RuntimeException throwIfGroupNotFound() {
        return new IllegalStateException("Group 'default' cannot be null!");
    }

    @NotNull
    private ImmutableContextSet.Builder newContextBuilder() {
        return ImmutableContextSet.builder()
                .add(CONTEXT_KEY, Boolean.TRUE.toString());
    }

    private void logResult(@NotNull Map<String, CustomMeta> metaMap, @NotNull Stopwatch stopwatch) {
        final StringJoiner joiner = new StringJoiner(", ");
        metaMap.forEach((key, value) -> joiner.add(key + "=" + value.defaultValue()));

        lpMetaPlus.getLogger().log(Level.INFO,
                "Metadata set for group 'default': " + joiner + " in {0}ms",
                stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
