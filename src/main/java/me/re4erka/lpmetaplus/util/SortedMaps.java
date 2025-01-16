package me.re4erka.lpmetaplus.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@UtilityClass
public final class SortedMaps {

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1) {
        return unmodifiableSortedMap(createMap(key1, value1));
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2) {
        return unmodifiableSortedMap(createMap(key1, value1, key2, value2));
    }

    @NotNull
    public  <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                      @NotNull K key3, @NotNull V value3) {
        return unmodifiableSortedMap(createMap(key1, value1, key2, value2, key3, value3));
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                     @NotNull K key3, @NotNull V value3, @NotNull K key4, @NotNull V value4) {
        return unmodifiableSortedMap(createMap(key1, value1, key2, value2, key3, value3, key4, value4));
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                     @NotNull K key3, @NotNull V value3, @NotNull K key4, @NotNull V value4,
                                     @NotNull K key5, @NotNull V value5) {
        return unmodifiableSortedMap(createMap(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5));
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                     @NotNull K key3, @NotNull V value3, @NotNull K key4, @NotNull V value4,
                                     @NotNull K key5, @NotNull V value5, @NotNull K key6, @NotNull V value6) {
        return unmodifiableSortedMap(
                createMap(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6)
        );
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                     @NotNull K key3, @NotNull V value3, @NotNull K key4, @NotNull V value4,
                                     @NotNull K key5, @NotNull V value5, @NotNull K key6, @NotNull V value6,
                                     @NotNull K key7, @NotNull V value7) {
        return unmodifiableSortedMap(
                createMap(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7)
        );
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                     @NotNull K key3, @NotNull V value3, @NotNull K key4, @NotNull V value4,
                                     @NotNull K key5, @NotNull V value5, @NotNull K key6, @NotNull V value6,
                                     @NotNull K key7, @NotNull V value7, @NotNull K key8, @NotNull V value8) {
        return unmodifiableSortedMap(
                createMap(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6,
                        value6, key7, value7, key8, value8)
        );
    }

    @NotNull
    public <K, V> SortedMap<K, V> of(@NotNull K key1, @NotNull V value1, @NotNull K key2, @NotNull V value2,
                                     @NotNull K key3, @NotNull V value3, @NotNull K key4, @NotNull V value4,
                                     @NotNull K key5, @NotNull V value5, @NotNull K key6, @NotNull V value6,
                                     @NotNull K key7, @NotNull V value7, @NotNull K key8, @NotNull V value8,
                                     @NotNull K key9, @NotNull V value9) {
        return unmodifiableSortedMap(
                createMap(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6,
                        value6, key7, value7, key8, value8, key9, value9)
        );
    }

    @NotNull
    @SafeVarargs
    public <K, V> SortedMap<K, V> ofEntries(@NotNull Map.Entry<K, V>... entries) {
        @SuppressWarnings("SortedCollectionWithNonComparableKeys")
        final TreeMap<K, V> map = new TreeMap<>();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }

        return unmodifiableSortedMap(map);
    }

    @NotNull
    private <K, V> SortedMap<K, V> unmodifiableSortedMap(@NotNull TreeMap<K, V> map) {
        return Collections.unmodifiableSortedMap(map);
    }

    @NotNull
    private <K, V> TreeMap<K, V> createMap(@NotNull Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of arguments passed. Key-value pairs expected.");
        }

        @SuppressWarnings("SortedCollectionWithNonComparableKeys")
        final TreeMap<K, V> map = new TreeMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked")
            final K key = (K) keyValues[i];
            @SuppressWarnings("unchecked")
            final V value = (V) keyValues[i + 1];

            map.put(key, value);
        }

        return map;
    }
}
