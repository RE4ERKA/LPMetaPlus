package me.re4erka.lpmetaplus;

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.re4erka.lpmetaplus.message.Message;

import java.util.Arrays;
import java.util.List;

@Getter
@Accessors(fluent = true)
@Configuration
@SuppressWarnings("FieldMayBeFinal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Messages {

    private Meta meta = new Meta();

    @Getter
    @Accessors(fluent = true)
    @Configuration
    public static final class Meta {

        private Message get = Message.of("У игрока &6%target% &fмета-данной &r%display_name% &fв количестве&8: &e%balance% %symbol%");
        private Message set = Message.of("Игроку &6%target% &fбыло установлено мета-данной &r%display_name%&8: &e%balance% %symbol%");
        private Message given = Message.of("Игроку &6%target% &fбыла выдана мета-данная &r%display_name%&8: &e%balance% %symbol%");
        private Message taken = Message.of("Игроку &6%target% &fбыло отнято мета-данных &r%display_name%&8: &e%balance% %symbol%");

        private Message notFound = Message.of("Введенная вами мета-данная &cне была найдена&f!");
    }

    private Command command = new Command();

    @Getter
    @Accessors(fluent = true)
    @Configuration
    public static final class Command {

        private Message unknownCommand = Message.of("Введенная вами команда &cне была найдена&f!");
        private Message tooManyArguments = Message.of("Вами введенно &cслишком много &fаргументов!");
        private Message notEnoughArguments = Message.of("Вами введенно &cнедостаточно &fаргументов!");
        private Message invalidArguments = Message.of("Введенные вами аргументы &cнекорректны&f! Используйте &e/lpmetaplus help &fдля помощи");

        private Message noPermission = Message.of("У вас &cнедостаточно прав&f, чтобы использовать эту команду!");

        private Message reloaded = Message.of("Плагин был &aуспешно перезагружен&f!");
        private List<Message> help = Arrays.asList(
                Message.of("Доступные команды&8:"),
                Message.of("&8- &e/lpmetaplus get &f<тип> <ник>"),
                Message.of("&8- &e/lpmetaplus set &f<тип> <количество> <ник> &7(-silent)"),
                Message.of("&8- &e/lpmetaplus take &f<тип> <количество> <ник> &7(-silent)"),
                Message.of("&8- &e/lpmetaplus give &f<тип> <количество> <ник> &7(-silent)"),
                Message.of("&8- &e/lpmetaplus reload")
        );
    }
}
