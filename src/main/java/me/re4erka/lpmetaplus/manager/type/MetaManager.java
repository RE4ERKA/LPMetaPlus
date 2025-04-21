package me.re4erka.lpmetaplus.manager.type;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.manager.LuckPermsProviderManager;
import me.re4erka.lpmetaplus.session.MetaSession;
import me.re4erka.lpmetaplus.util.OfflineUUID;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MetaManager extends LuckPermsProviderManager {

    private final Group defaultGroup;

    public MetaManager(@NotNull LPMetaPlus lpMetaPlus) {
        super(lpMetaPlus);

        this.defaultGroup = groupManager.getGroup(DEFAULT_GROUP_NAME);
        throwIfDefaultGroupNotFound(defaultGroup);
    }

    @NotNull
    @SuppressWarnings("ConstantConditions")
    public MetaSession getUser(@NotNull OfflinePlayer player) {
        return getUser(player.getUniqueId());
    }

    @NotNull
    @SuppressWarnings("ConstantConditions")
    public MetaSession getUser(@NotNull UUID uuid) {
        final User cachedUser = userManager.getUser(uuid);
        throwIfUserIsNull(cachedUser);
        return openSession(cachedUser, false);
    }

    @NotNull
    public CompletableFuture<MetaSession> findUser(@NotNull UUID uuid, @NotNull String username) {
        final User cachedUser = userManager.getUser(uuid);
        if (cachedUser == null) {
            return CompletableFuture.supplyAsync(() -> openSession(loadUser(uuid, username), true));
        } else {
            return CompletableFuture.completedFuture(openSession(cachedUser, false));
        }
    }

    @NotNull
    public CompletableFuture<MetaSession> findUser(@NotNull String username) {
        final User cachedUser = userManager.getUser(username);
        if (cachedUser == null) {
            return CompletableFuture.supplyAsync(() -> openSession(lookup(username), true));
        } else {
            return CompletableFuture.completedFuture(openSession(cachedUser, false));
        }
    }

    public void registerWarningEvents() {
        final EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(lpMetaPlus, NodeRemoveEvent.class, this::warnIfRemovedPlusMetaFromGroup);
        eventBus.subscribe(lpMetaPlus, NodeClearEvent.class, this::warnIfRemovedPlusMetaFromGroup);
    }

    @NotNull
    public MetaSession openSession(@NotNull User user, boolean lookup) {
        return new MetaSession(lpMetaPlus, userManager, actionLogger, defaultGroup, user, lookup);
    }

    private void warnIfRemovedPlusMetaFromGroup(@NotNull NodeMutateEvent nodeMutateEvent) {
        if (nodeMutateEvent.isGroup() && nodeMutateEvent.getDataType() == DataType.NORMAL) {
            final Set<Node> nodesBefore = nodeMutateEvent.getDataBefore().stream()
                    .filter(node -> node.getContexts().containsKey(LuckPermsProviderManager.CONTEXT_KEY))
                    .collect(Collectors.toSet());

            if (!nodesBefore.isEmpty()) {
                final Set<Node> nodesAfter = nodeMutateEvent.getDataAfter().stream()
                        .filter(node -> node.getContexts().containsKey(LuckPermsProviderManager.CONTEXT_KEY))
                        .collect(Collectors.toSet());

                if (nodesBefore.size() != nodesAfter.size()) {
                    final Set<Node> removedNodes = nodesBefore.stream()
                            .filter(nodeBefore -> nodesAfter.stream()
                                    .noneMatch(nodeAfter -> nodeAfter.getKey().equals(nodeBefore.getKey())))
                            .collect(Collectors.toSet());
                    logWarning(removedNodes);
                }
            }
        }
    }

    private void logWarning(@NotNull Set<Node> removedNodes) {
        final StringJoiner joiner = new StringJoiner("', '");
        removedNodes.forEach(node -> joiner.add(node.getKey()));

        lpMetaPlus.getLogger().log(Level.WARNING, "Probably the meta that is used "
                + "by LPMetaPlus plugin has been unset/cleared, without it the plugin may not work properly. "
                + "Please return the meta ['" + joiner + "'] or restart the plugin if you are not sure.");
    }

    @NotNull
    @Blocking
    private User lookup(@NotNull String username) {
        UUID uuid = userManager.lookupUniqueId(username).join();
        if (uuid == null) {
            uuid = OfflineUUID.fromName(username);
        }

        return loadUser(uuid, username);
    }

    @NotNull
    @Blocking
    private User loadUser(@NotNull UUID uuid, @NotNull String username) {
        final User user = userManager.loadUser(uuid, username).join();
        user.auditTemporaryNodes();

        return user;
    }

    private void throwIfDefaultGroupNotFound(@Nullable Group group) {
        if (group == null) {
            lpMetaPlus.logError("When trying to get the default group, a null was get, " +
                    "which should not be the case.");
            throw new IllegalStateException("Default group cannot be null");
        }
    }

    private void throwIfUserIsNull(@Nullable User user) {
        if (user == null) {
            lpMetaPlus.logError("An error occurred when trying to get a user by UUID. " +
                    "The method being called assumes that the player being retrieved will be online, " +
                    "maybe the problem is related to this");
            throw new NullPointerException("A user cannot be null!");
        }
    }
}
