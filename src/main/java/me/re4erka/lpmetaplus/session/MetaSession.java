package me.re4erka.lpmetaplus.session;

import lombok.Getter;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.action.MetaAction;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.actionlog.ActionLogger;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class MetaSession implements AutoCloseable {

    private final LPMetaPlus lpMetaPlus;

    private final UserManager userManager;
    private final ActionLogger actionLogger;

    private final Group defaultGroup;

    private final User user;
    @Getter
    private final boolean lookup;

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private static final String ZERO = "0";

    public MetaSession(@NotNull LPMetaPlus lpMetaPlus,
                       @NotNull UserManager userManager,
                       @NotNull ActionLogger actionLogger,
                       @NotNull Group defaultGroup,
                       @NotNull User user, boolean lookup) {
        this.lpMetaPlus = lpMetaPlus;
        this.userManager = userManager;
        this.actionLogger = actionLogger;

        this.defaultGroup = defaultGroup;

        this.user = user;
        this.lookup = lookup;
    }

    public int get(@NotNull String key) {
        return zeroOrGreaterParse(
                getAsString(key));
    }

    @NotNull
    public String getAsString(@NotNull String key) {
        return getFirst(key)
                .getMetaValue();
    }

    public void edit(@NotNull Consumer<Editor> edit, boolean silent) {
        final Editor editor = new Editor(silent);
        edit.accept(editor);
        saveUser().thenRun(() -> logAction(editor));
    }

    @Override
    public void close() {
        if (lookup) {
            userManager.cleanupUser(user);
        }
    }

    @NotNull
    private MetaNode getFirst(@NotNull String key) {
        return metas().stream()
                .filter(node -> isEquals(node, key))
                .findFirst()
                .orElse(getFirstFromDefaultGroup(key));
    }

    @NotNull
    @SuppressWarnings("ConstantConditions")
    private MetaNode getFirstFromDefaultGroup(@NotNull String key) {
        return defaultGroup.getNodes(NodeType.META).stream()
                .filter(node -> isEquals(node, key))
                .findFirst()
                .orElseThrow(() -> throwMetaNotFound(key));
    }

    @NotNull
    private Collection<MetaNode> metas() {
        return user.getNodes(NodeType.META);
    }

    private boolean isEquals(@NotNull MetaNode node, @NotNull String key) {
        return node.getMetaKey().equals(key.toLowerCase());
    }

    private void logAction(@NotNull Editor editor) {
        final Set<MetaAction> actions = editor.actions;

        if (editor.silent) {
            actions.forEach(action -> actionLogger.submitToStorage(buildAction(action)));
        } else {
            actions.forEach(action -> actionLogger.submit(buildAction(action)));
        }
    }

    @NotNull
    private Action buildAction(@NotNull MetaAction action) {
        return actionLogger.actionBuilder()
                .timestamp(action.timestamp())
                .source(CONSOLE_UUID)
                .sourceName("LPMetaPlus")
                .target(user.getUniqueId())
                .targetName(user.getFriendlyName())
                .targetType(Action.Target.Type.USER)
                .description(action.toDescription())
                .build();
    }

    @NotNull
    private CompletableFuture<Void> saveUser() {
        return userManager.saveUser(user);
    }

    private int zeroOrGreaterParse(@NotNull String value) {
        final int i = Integer.parseInt(value);
        return Math.max(i, 0);
    }

    private RuntimeException throwMetaNotFound(@NotNull String key) {
        lpMetaPlus.logError("When trying to find meta for a user, it was not found. "
                + "This meta may have been unset/cleared from the 'default' group, "
                + "please restart the plugin or return the meta: '" + key + "'");
        return new IllegalStateException("MetaNode cannot be null!");
    }

    public final class Editor {

        private final Set<MetaAction> actions;
        private final boolean silent;

        private Editor(boolean silent) {
            this.actions = new HashSet<>(1);
            this.silent = silent;
        }

        public void set(@NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int count) {
            remove(key);
            addNode(buildMetaNode(key, zeroOrGreaterToString(count)));
            addAction(buildMetaAction(MetaAction.Type.SET, key, count));
        }

        public void give(@NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int count) {
            removeThen(key, previousCount -> {
                addNode(buildMetaNode(key, zeroOrGreaterToString(previousCount + count)));
                addAction(buildMetaAction(MetaAction.Type.GIVE, key, count));
            });
        }

        public void take(@NotNull String key, @Range(from = 0, to = Integer.MAX_VALUE) int count) {
            removeThen(key, previousCount -> {
                addNode(buildMetaNode(key, zeroOrGreaterToString(previousCount - count)));
                addAction(buildMetaAction(MetaAction.Type.TAKE, key, count));
            });
        }

        public void reset(@NotNull String key) {
            removeNode(getFirst(key));
            addAction(buildResetMetaAction(key));
        }

        public int remove(@NotNull String key) {
            final MetaNode node = getFirst(key);
            final String value = node.getMetaValue();
            removeNode(node);

            return Integer.parseInt(value);
        }

        public void removeThen(@NotNull String key, @NotNull Consumer<Integer> then) {
            then.accept(remove(key));
        }

        @NotNull
        private MetaNode buildMetaNode(@NotNull String key, @NotNull String value) {
            return MetaNode.builder()
                    .key(key.toLowerCase()).value(value)
                    .build();
        }

        private void addNode(@NotNull Node node) {
            nodeMap().add(node);
        }

        private void removeNode(@NotNull Node node) {
            nodeMap().remove(node);
        }

        @NotNull
        private NodeMap nodeMap() {
            return user.getData(DataType.NORMAL);
        }

        @NotNull
        private MetaAction buildMetaAction(@NotNull MetaAction.Type type, @NotNull String key, int count) {
            return MetaAction.builder()
                    .type(type).key(key).count(count)
                    .build();
        }

        @NotNull
        private MetaAction buildResetMetaAction(@NotNull String key) {
            return MetaAction.builder()
                    .type(MetaAction.Type.RESET).key(key)
                    .build();
        }

        private void addAction(@NotNull MetaAction action) {
            actions.add(action);
        }

        @NotNull
        private String zeroOrGreaterToString(int i) {
            return i < 0 ? ZERO : Integer.toString(i);
        }
    }
}
