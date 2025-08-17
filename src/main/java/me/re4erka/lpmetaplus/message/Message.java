package me.re4erka.lpmetaplus.message;

import me.re4erka.lpmetaplus.message.placeholder.Placeholders;
import me.re4erka.lpmetaplus.util.Formatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class Message {

    private final String text;

    private static final Message EMPTY = of(StringUtils.EMPTY);

    private Message(@NotNull String text) {
        this.text = text;
    }

    @NotNull
    public static Message of(@NotNull String text) {
        return new Message(text);
    }

    @NotNull
    public static Message empty() {
        return EMPTY;
    }

    public void send(@NotNull CommandSender sender) {
        if (isEmpty()) {
            return;
        }

        send(sender, text);
    }

    public void send(@NotNull CommandSender sender, @NotNull Placeholders placeholders) {
        if (isEmpty()) {
            return;
        }

        send(sender, placeholders.process(text));
    }

    public boolean isEmpty() {
        return this == EMPTY || text.isEmpty();
    }

    private void send(@NotNull CommandSender sender, @NotNull String text) {
        sender.sendMessage(isConsoleSender(sender) ? Formatter.strip(text) : Formatter.format(text));
    }

    private boolean isConsoleSender(@NotNull CommandSender sender) {
        return Bukkit.getConsoleSender() == sender;
    }

    public static class Serializer implements de.exlll.configlib.Serializer<Message, String> {

        @NotNull
        @Override
        public String serialize(@NotNull Message message) {
            return message.text;
        }

        @NotNull
        @Override
        public Message deserialize(String value) {
            return new Message(value);
        }
    }
}
