package me.re4erka.lpmetaplus.util;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Key implements CharSequence, Comparable<Key> {

    private final byte[] characters;
    private final int hash; // Кэшируем хэш-код для производительности

    public Key(@NotNull String input) {
        throwIfNotAscii(input);
        this.characters = toUpperCaseAscii(input);
        this.hash = computeHashCode();
    }

    private Key(byte[] characters) {
        this.characters = characters;
        this.hash = computeHashCode();
    }

    @NotNull
    public Key append(@NotNull String suffix) {
        return createWithNewString(suffix, false);
    }

    @NotNull
    public Key prepend(@NotNull String prefix) {
        return createWithNewString(prefix, true);
    }

    @NotNull
    private Key createWithNewString(@NotNull String additional, boolean prepend) {
        throwIfNotAscii(additional);

        final byte[] additionalBytes = toUpperCaseAscii(additional);
        final byte[] newCharacters = new byte[this.characters.length + additionalBytes.length];

        if (prepend) {
            System.arraycopy(additionalBytes, 0, newCharacters, 0, additionalBytes.length);
            System.arraycopy(this.characters, 0, newCharacters, additionalBytes.length, this.characters.length);
        } else {
            System.arraycopy(this.characters, 0, newCharacters, 0, this.characters.length);
            System.arraycopy(additionalBytes, 0, newCharacters, this.characters.length, additionalBytes.length);
        }

        return new Key(newCharacters);
    }

    private boolean isAscii(@NotNull String input) {
        for (char c : input.toCharArray()) {
            if (c > 127) {
                return false;
            }
        }

        return true;
    }

    private byte[] toUpperCaseAscii(@NotNull String input) {
        final byte[] bytes = input.getBytes(StandardCharsets.US_ASCII);

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] >= 'a' && bytes[i] <= 'z') {
                bytes[i] = (byte) (bytes[i] - ('a' - 'A'));
            }
        }

        return bytes;
    }

    private void throwIfNotAscii(@NotNull String input) {
        if (!isAscii(input)) {
            throw new IllegalArgumentException("Input contains non-ASCII characters");
        }
    }

    @Override
    public int length() {
        return characters.length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= characters.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + characters.length);
        }

        return (char) characters[index];
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end > characters.length || start > end) {
            throw new IndexOutOfBoundsException("Start: " + start + ", End: " + end + ", Length: " + characters.length);
        }

        return new String(characters, start, end - start, StandardCharsets.US_ASCII);
    }

    @NotNull
    @Override
    public String toString() {
        return new String(characters, StandardCharsets.US_ASCII);
    }

    @NotNull
    public String toLowerCase() {
        final byte[] lowerCaseBytes = new byte[characters.length];

        for (int i = 0; i < characters.length; i++) {
            final byte currentByte = characters[i];

            if (currentByte >= 'A' && currentByte <= 'Z') {
                lowerCaseBytes[i] = (byte) (currentByte + ('a' - 'A'));
            } else {
                lowerCaseBytes[i] = currentByte;
            }
        }

        return new String(lowerCaseBytes, StandardCharsets.US_ASCII);
    }

    @Override
    public int compareTo(final Key other) {
        final int len1 = this.characters.length;
        final int len2 = other.characters.length;

        final int lim = Math.min(len1, len2);

        for (int i = 0; i < lim; i++) {
            final int b1 = this.characters[i] & 0xFF;
            final int b2 = other.characters[i] & 0xFF;

            if (b1 != b2) {
                return b1 - b2;
            }
        }

        return len1 - len2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Key other = (Key) obj;
        return Arrays.equals(this.characters, other.characters);
    }

    public boolean equals(CharSequence charSequence) {
        if (charSequence == null) {
            return false;
        }

        if (charSequence.length() != characters.length) {
            return false;
        }

        if (charSequence instanceof String) {
            return charSequence.equals(
                    toString()
            );
        }

        final int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if ((char) characters[i] != charSequence.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private int computeHashCode() {
        int result = 1;

        for (byte b : characters) {
            result = 31 * result + (b & 0xFF);
        }

        return result;
    }

    @NotNull
    public static Key of(final @NotNull String input) {
        return new Key(input);
    }
}
