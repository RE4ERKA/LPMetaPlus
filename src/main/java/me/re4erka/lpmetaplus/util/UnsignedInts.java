package me.re4erka.lpmetaplus.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class UnsignedInts {

    public boolean is(int amount) {
        return amount > 0;
    }

    public boolean isNot(int amount) {
        return !is(amount);
    }
}
