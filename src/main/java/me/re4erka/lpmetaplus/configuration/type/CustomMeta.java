package me.re4erka.lpmetaplus.configuration.type;

import de.exlll.configlib.Configuration;
import lombok.*;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.message.Message;
import me.re4erka.lpmetaplus.message.placeholder.Placeholders;
import me.re4erka.lpmetaplus.message.placeholder.provider.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Accessors(fluent = true)
@SuppressWarnings("FieldMayBeFinal")
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomMeta implements PlaceholderProvider {

    @Nullable
    @Builder.Default
    private String displayName = null;
    @Builder.Default
    private int defaultValue = 0;
    @Nullable
    @Builder.Default
    private Character symbol = null;

    @Getter(value = AccessLevel.NONE)
    private Map<String, String> defaultContexts = null;

    @NotNull
    public String defaultValueToString() {
        return Integer.toString(defaultValue);
    }

    @NotNull
    public Map<String, String> defaultContexts() {
        return defaultContexts == null
                ? Collections.emptyMap() : defaultContexts;
    }

    public boolean isCommandEnabled() {
        return command != null && command.enabled;
    }

    @Override
    public Placeholders.Builder placeholdersBuilder() {
        return Placeholders.builder()
                .add("display_name", displayName)
                .add("default_value", defaultValue)
                .add("symbol", symbol);
    }

    private Command command = new Command();

    @Getter
    @Accessors(fluent = true)
    @Builder
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Command {

        @Builder.Default
        private boolean enabled = false;
        @Nullable
        @Builder.Default
        private String permission = null;
        @NotNull
        @Builder.Default
        private Message message = Message.empty();
        @NotNull
        @Builder.Default
        private List<String> alias = Collections.emptyList();
    }
}
