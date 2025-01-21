package me.re4erka.lpmetaplus.migration.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class MigrationData {

    private final UUID uuid;
    private final String username;

    private final int balance;
}
