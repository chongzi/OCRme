package com.ashomok.ocrme.utils;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());

        List<Map.Entry<K, V>> sorted = Stream.of(list).sortBy(Map.Entry::getValue).toList();

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : sorted) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
