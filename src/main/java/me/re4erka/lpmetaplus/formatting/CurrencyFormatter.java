package me.re4erka.lpmetaplus.formatting;

import me.re4erka.lpmetaplus.LPMetaPlus;
import me.re4erka.lpmetaplus.Settings;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

public class CurrencyFormatter {

    private final Settings.Formatting formatting;

    private final Map<SuffixType, String> suffixes;
    private final int magnitude;

    public CurrencyFormatter(@NotNull LPMetaPlus lpMetaPlus) {
        this.formatting = lpMetaPlus.settings().formatting();

        this.suffixes = formatting.compact()
                .map(Settings.Formatting.Compact::suffixesToEnumMap)
                .orElse(Collections.emptyMap());
        this.magnitude = formatting.compact()
                .map(Settings.Formatting.Compact::magnitude)
                .map(i -> Math.min(i, 3))
                .orElse(0);
    }

    @NotNull
    public String format(int balance, @Nullable Character symbol) {
        final String formattedBalance = formatting.isCompactEnabled()
                ? formatCompact(balance)
                : formatDefault(balance);

        return String.format(formatting.output(), formattedBalance,
                symbol == null ? StringUtils.EMPTY : symbol);
    }

    @NotNull
    private String formatCompact(int value) {
        if (value >= 1_000_000_000) {
            return formatWithSuffix(value, SuffixType.BILLION);
        } else if (value >= 1_000_000) {
            return formatWithSuffix(value, SuffixType.MILLION);
        } else if (value >= 1_000) {
            return formatWithSuffix(value, SuffixType.THOUSAND);
        } else {
            return String.valueOf(value);
        }
    }

    @NotNull
    private String formatDefault(int value) {
        final String rawValue = String.valueOf(value);
        final int length = rawValue.length();

        final StringBuilder builder = new StringBuilder(length + length / 3);
        for (byte i = 0; i < length; i++) {
            if (i > 0 && (length - i) % 3 == 0) {
                builder.append(formatting.separator());
            }

            builder.append(rawValue.charAt(i));
        }

        return builder.toString();
    }

    @NotNull
    private String formatWithSuffix(int balance, @NotNull SuffixType suffix) {
        final int wholePart = calculateWholePart(balance, suffix.divisor());
        final int fractionalPart = calculateFractionalPart(balance, suffix.divisor());

        final StringBuilder result = new StringBuilder();
        result.append(wholePart);

        if (fractionalPart > 0) {
            result.append(formatting.separator())
                    .append(fractionalPart);
        }

        final String value = suffixes.get(suffix);
        if (value != null) {
            result.append(value);
        }

        return result.toString();
    }

    private int calculateWholePart(int balance, int divisor) {
        return balance / divisor;
    }

    private int calculateFractionalPart(int balance, int divisor) {
        return (balance % divisor) * (int) Math.pow(10, magnitude) / divisor;
    }
}
