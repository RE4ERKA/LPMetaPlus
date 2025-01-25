package me.re4erka.lpmetaplus.emulation.exception;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NotEmulatedException extends IllegalStateException {
    public NotEmulatedException(@NotNull String method) {
        super("Emulation of " + method + "() method is not implemented!");
    }

    public NotEmulatedException(@NotNull String method, boolean supportIgnore) {
        super("Emulation of " + method + "() method is not implemented!"
                + (supportIgnore ? StringUtils.EMPTY : " Can't be ignored."));
    }
}
