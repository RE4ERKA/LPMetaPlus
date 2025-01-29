package me.re4erka.lpmetaplus.formatting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum SuffixType {
    THOUSAND(1_000_000_000),
    MILLION(1_000_000),
    BILLION(1_000);

    private final int divisor;
}
