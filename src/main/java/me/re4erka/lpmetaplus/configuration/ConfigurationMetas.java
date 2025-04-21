package me.re4erka.lpmetaplus.configuration;

import com.google.common.collect.Lists;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.configuration.type.CustomMeta;
import me.re4erka.lpmetaplus.message.Message;
import me.re4erka.lpmetaplus.util.SortedMaps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Getter
@Accessors(fluent = true)
@SuppressWarnings("FieldMayBeFinal")
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationMetas {

    private Map<String, CustomMeta> types = SortedMaps.of(
            "RUBIES", CustomMeta.builder()
                    .displayName("&cРубины")
                    .symbol('◆')
                    .command(CustomMeta.Command.builder()
                            .enabled(true)
                            .permission("lpmetaplus.command.rubies")
                            .message(Message.of("Ваш баланс: &c%balance% %symbol%"))
                            .alias(Lists.newArrayList("ruby", "рубины"))
                            .build())
                    .build(),
            "RUBLES", CustomMeta.builder()
                    .displayName("&aРубли")
                    .symbol('₽')
                    .command(CustomMeta.Command.builder()
                            .enabled(true)
                            .message(Message.of("Ваш баланс: &a%balance% %symbol%"))
                            .alias(Lists.newArrayList("ruble", "рубли"))
                            .build())
                    .build(),
            "FLOWERS", CustomMeta.builder()
                    .displayName("&eЦветочки")
                    .symbol('❀')
                    .defaultContexts(SortedMaps.of("world", "spawn"))
                    .build()
    );

    @NotNull
    public Optional<CustomMeta> get(@NotNull String type) {
        return Optional.ofNullable(
                getIfPresent(type));
    }

    @Nullable
    public CustomMeta getIfPresent(@NotNull String type) {
        final String key = type.toUpperCase(Locale.ROOT);
        return types.get(key);
    }

    public boolean contains(@NotNull String key) {
        return types.containsKey(key);
    }

    @NotNull
    @Unmodifiable
    public List<String> keys() {
        return Collections.unmodifiableList(
                new ArrayList<>(types.keySet()));
    }
}