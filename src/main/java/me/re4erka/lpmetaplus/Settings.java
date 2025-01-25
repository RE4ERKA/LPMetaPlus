package me.re4erka.lpmetaplus;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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

    @Comment({"Эмуляция методов из API других плагинов на донатную валюту.",
            "Не влияет на производительность.",
            ""})
    private Emulation emulation = new Emulation();

    @Getter
    @Accessors(fluent = true)
    @Configuration
    public static final class Emulation {

        @Comment({"Какая мета будет по-дефолту для эмуляции?",
                "Необходимо указать для: PLAYER_POINTS"})
        private String defaultMeta = "RUBIES";

        @Comment({"Выполнять ли методы из эмуляции всегда принудительно в тихом режиме?",
                "Если включено, то не будет логгирования от LuckPerms в консоли."})
        private boolean forcedSilent = false;

        @Comment({"Игнорировать ли методы которые нереализованы для эмуляции?",
                "Если включено, то не будет выбрасываться исключение NotEmulatedException."})
        private boolean ignoreNotEmulatedMethods = false;
    }
}
