package me.re4erka.lpmetaplus;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.formatting.SuffixType;
import me.re4erka.lpmetaplus.util.SortedMaps;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Accessors(fluent = true)
@Configuration
@SuppressWarnings("FieldMayBeFinal")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Settings {

    @Comment({"Миграция с других плагинов на донатную валюту.",
            "Например: /lpmetaplus migrate PLAYER_POINTS SQLITE"})
    private Migration migration = new Migration();

    @Getter
    @Accessors(fluent = true)
    @Configuration
    public static final class Migration {

        @Comment({"Тип мета-данной по-умолчанию для миграции.",
                "Какой тип донатной валюты будет мигрироваться из плагина PlayerPoints?",
                "Требуется обязательно указать существующий тип плагина LPMetaPlus."})
        private String defaultType = "RUBIES";

        @Comment("Настройка подключения к базе-данных для миграции.")
        private Credentials credentials = new Credentials();

        @Getter
        @Accessors(fluent = true)
        @Configuration
        public static final class Credentials {

            private String host = "localhost";
            private int port = 3306;

            @Comment("Название базы-данных плагина для миграции.")
            private String database = "points";
            private String username = "root";
            private String password = "password";
        }
    }

    @Comment("Форматирование чисел донатной валюты.")
    private Formatting formatting = new Formatting();

    @Getter
    @Accessors(fluent = true)
    @Configuration
    public static final class Formatting {

        @Comment({"Итоговый вывод отформатированного числа.",
                "%s - является значением валюты.",
                "%c - является символов валюты."})
        private String output = "%s %c";

        @Comment({"Разделитель числа валюты.",
                "Если указать пустым, то отключено."})
        private char separator = '.';

        public boolean isCompactEnabled() {
            return compact != null && compact.enabled;
        }

        public Optional<Compact> compact() {
            return Optional.ofNullable(compact);
        }

        @Getter(value = AccessLevel.NONE)
        @Comment({"Форматирование числа компактным методом.",
                "К примеру: \"1.4M ◆\" или \"1.5K ◆\""})
        private Compact compact = new Compact();

        @Getter
        @Accessors(fluent = true)
        @Configuration
        public static final class Compact {

            @Comment("Включить ли компактность?")
            private boolean enabled = false;

            @Comment({"Длина числа после раздителя.",
                    "Если указано 0, то отключено."})
            private int magnitude = 1;

            @Comment("Отображение суффиксов.")
            private Map<SuffixType, String> suffixes = SortedMaps.of(
                    SuffixType.THOUSAND, "K",
                    SuffixType.MILLION, "M",
                    SuffixType.BILLION, "B");

            public Map<SuffixType, String> suffixesToEnumMap() {
                final EnumMap<SuffixType, String> map = new EnumMap<>(SuffixType.class);
                map.putAll(suffixes);
                return Collections.unmodifiableMap(map);
            }
        }
    }

    @Comment({"Эмуляция методов из API других плагинов на донатную валюту.",
            "Не влияет на производительность."})
    private Emulation emulation = new Emulation();

    @Getter
    @Accessors(fluent = true)
    @Configuration
    public static final class Emulation {

        @Comment({"Какой тип мета-данной будет по-дефолту для эмуляции?",
                "Необходимо указать для: PLAYER_POINTS"})
        private String defaultType = "RUBIES";

        @Comment({"Выполнять ли методы из эмуляции всегда принудительно в тихом режиме?",
                "Если включено, то не будет логгирования от LuckPerms в консоли."})
        private boolean forcedSilent = false;

        @Comment({"Игнорировать ли методы которые нереализованы для эмуляции?",
                "Если включено, то не будет выбрасываться исключение NotEmulatedException."})
        private boolean ignoreNotEmulatedMethods = false;
    }
}
