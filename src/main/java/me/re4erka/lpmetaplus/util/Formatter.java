package me.re4erka.lpmetaplus.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class Formatter {

    @NotNull
    public String format(@NotNull String input) {
        if (input.isEmpty()) {
            return "";
        }

        final StringBuilder result = new StringBuilder(input.length());
        final char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            final char current = chars[i];
            if (current == '&' && i + 1 < chars.length) {

                final char next = chars[i + 1];
                if (isColorCode(next)) {
                    result.append('§').append(next);
                    i++;
                } else {
                    result.append(current);
                }
            } else if (current == '#' && i + 6 < chars.length && isHexColor(chars, i + 1)) {
                result.append("§x");
                for (int j = 0; j < 6; j++) {
                    result.append('§').append(chars[i + 1 + j]);
                }

                i += 6;
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }

    private boolean isColorCode(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')
                || c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o' || c == 'r';
    }

    private boolean isHexColor(char[] chars, int start) {
        if (start + 5 >= chars.length) {
            return false;
        }

        for (int i = start; i < start + 6; i++) {
            if (!isHexChar(chars[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean isHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
}
