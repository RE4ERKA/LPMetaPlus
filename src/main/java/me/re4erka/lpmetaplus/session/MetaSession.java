package me.re4erka.lpmetaplus.session;

import lombok.Getter;
import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.action.MetaAction;
import me.re4erka.lpmetaplus.util.Key;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.actionlog.ActionLogger;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class MetaSession implements AutoCloseable {

    private final LPMetaPlus lpMetaPlus;

    private final UserManager userManager;
    private final ActionLogger actionLogger;

    private final User user;
    @Getter
    private final boolean lookup;

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    public MetaSession(@NotNull LPMetaPlus lpMetaPlus,
                       @NotNull UserManager userManager,
                       @NotNull ActionLogger actionLogger,
                       @NotNull User user, boolean lookup) {
        this.lpMetaPlus = lpMetaPlus;
        this.userManager = userManager;
        this.actionLogger = actionLogger;

        this.user = user;
        this.lookup = lookup;
    }

    public int get(@NotNull Key key) {
        return Integer.parseUnsignedInt(
                getAsString(key)
        );
    }

    @NotNull
    public String getAsString(@NotNull Key key) {
        return findFirst(key)
                .map(MetaNode::getMetaValue)
                .orElse("0");
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
    private Optional<MetaNode> findFirst(@NotNull Key key) {
        return metas().stream()
                .filter(node -> isEquals(node, key))
                .findFirst();
    }

    @NotNull
    private Collection<MetaNode> metas() {
        return user.getNodes(NodeType.META);
    }

    private boolean isEquals(@NotNull MetaNode node, @NotNull Key key) {
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

    private CompletableFuture<Void> saveUser() {
        return userManager.saveUser(user);
    }

    private RuntimeException throwIfMetaNotFound(@NotNull Key key) {
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

        public void set(@NotNull Key key, int count) {
            remove(key);
            addNode(buildMetaNode(key, Integer.toUnsignedString(count)));
            addAction(buildMetaAction(MetaAction.Type.SET, key, count));
        }

        public void give(@NotNull Key key, int count) {
            removeThen(key, previousCount -> {
                addNode(buildMetaNode(key, Integer.toUnsignedString(previousCount + count)));
                addAction(buildMetaAction(MetaAction.Type.GIVE, key, count));
            });
        }

        public void take(@NotNull Key key, int count) {
            removeThen(key, previousCount -> {
                addNode(buildMetaNode(key, Integer.toUnsignedString(previousCount - count)));
                addAction(buildMetaAction(MetaAction.Type.TAKE, key, count));
            });
        }

        public int remove(@NotNull Key key) {
            return findFirst(key)
                    .map(node -> {
                        final String value = node.getMetaValue();
                        removeNode(node);
                        return value;
                    })
                    .map(Integer::parseUnsignedInt)
                    .orElse(0);
        }

        public void removeThen(@NotNull Key key, @NotNull Consumer<Integer> then) {
            then.accept(remove(key));
        }

        @NotNull
        private MetaNode buildMetaNode(@NotNull Key key, @NotNull String value) {
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
        private MetaAction buildMetaAction(@NotNull MetaAction.Type type, @NotNull Key key, int count) {
            return MetaAction.builder()
                    .type(type).key(key).count(count)
                    .build();
        }

        private void addAction(@NotNull MetaAction action) {
            actions.add(action);
        }
    }
}
