package ru.uniserg.graphaml.utils;

import java.util.*;
import java.util.stream.*;

public class MapUtils {

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        if (keys == null || values == null) {
            throw new IllegalArgumentException("Списки не могут быть null");
        }

        return IntStream.range(0, Math.min(keys.size(), values.size()))
                .boxed()
                .collect(Collectors.toMap(
                        keys::get,
                        values::get,
                        (oldVal, newVal) -> newVal // Перезаписывает дубликаты ключей
                ));
    }
}