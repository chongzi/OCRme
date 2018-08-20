package com.ashomok.ocrme.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class MapUtilTest {
    private Map<String, String> map;

    private Map<String, String> ru_map;

    @Before
    public void before() {
        map = new HashMap<>();
        map.put("az", "Azerbajan");
        map.put("en", "English");
        map.put("ukr", "Ukrainina");
        map.put("pl", "Polish");
        map.put("af", "Africans");

        ru_map = new HashMap<>();
        ru_map.put("en", "Английский");
        ru_map.put("ukr", "Украинский");
        ru_map.put("pl", "Польский");
        ru_map.put("af", "Африканский");
        ru_map.put("az", "Азеррбайджан");
    }

    @Test
    public void testSortByValue() {
        Map<String, String> sorted = MapUtil.sortByValue(map);

        Map.Entry<String, String> entry = sorted.entrySet().iterator().next();
        String value = entry.getValue();

        Assert.assertTrue(value.equals("Africans"));

    }

    @Test
    public void testSortByValueRu() {
        Map<String, String> sorted = MapUtil.sortByValue(ru_map);

        Map.Entry<String, String> entry = sorted.entrySet().iterator().next();
        String value = entry.getValue();

        Assert.assertTrue(value.equals("Азеррбайджан"));
    }
}