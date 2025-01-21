package me.re4erka.lpmetaplus.manager;

import me.re4erka.lpmetaplus.LPMetaPlus;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.actionlog.ActionLogger;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import org.jetbrains.annotations.NotNull;

public abstract class LuckPermsProviderManager {

    protected final LPMetaPlus lpMetaPlus;
    protected final LuckPerms luckPerms;

    protected final UserManager userManager;
    protected final GroupManager groupManager;

    protected final ActionLogger actionLogger;

    public static final String CONTEXT_KEY = "meta-plus";
    protected static final String DEFAULT_GROUP_NAME = "default";

    protected LuckPermsProviderManager(@NotNull LPMetaPlus lpMetaPlus) {
        this.lpMetaPlus = lpMetaPlus;

        throwIfLuckPermsProviderClassNotFound();
        this.luckPerms = LuckPermsProvider.get();

        this.userManager = luckPerms.getUserManager();
        this.groupManager = luckPerms.getGroupManager();

        this.actionLogger = luckPerms.getActionLogger();
    }

    private void throwIfLuckPermsProviderClassNotFound() {
        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
        } catch (ClassNotFoundException exception) {
            lpMetaPlus.logError("The class LuckPermsProvider was not found! "
                    + "Probably an outdated version of LuckPerms is used, "
                    + "please install version 5.0 or higher.");
            throw new RuntimeException(exception);
        } catch (IllegalStateException exception) {
            lpMetaPlus.logError("The LuckPermsProvider class has not been loaded! "
                    + "Probably the LuckPerms plugin is not specified as a dependency "
                    + "in the plugin.yml or paper-plugin.yml file.");
            throw new RuntimeException(exception);
        }
    }
}
