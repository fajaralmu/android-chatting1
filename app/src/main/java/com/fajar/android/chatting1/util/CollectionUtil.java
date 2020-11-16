package com.fajar.android.chatting1.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionUtil {
    public static <T> List<T> arrayToList(T... array) {
        List<T> list = new ArrayList<T>();
        for (T t : array) {
            list.add(t);
        }

        return list;

    }

    public static <T> List<T> prepend(List<T> list, T value){

        List<T> result = new ArrayList<>();
        result.add(value);
        result.addAll(list);
        return result;
    }

    public static void main(String[] args) {

    }

    public static <K, T> List<T> mapToList(Map<K, T> map) {
        List<T> list = new ArrayList<T>();
        if(null != map)
            for (K key : map.keySet()) {
                list.add(map.get(key));
            }

        return list;
    }

    public static <K, T> List<T> mapOfListToList(Map<K, List<T>> map) {
        List<T> list = new ArrayList<T>();
        for (K key : map.keySet()) {
            List<T> mapValue = map.get(key);
            if (null == mapValue)
                continue;

            list.addAll(mapValue);
        }

        return list;
    }

    public static <T> void printArray(T[] array) {
        if (null == array) {
            return;
        }

        String[] arrayString = toArrayOfString(array);

    }

    public static <T> List<T> listOf(T o) {

        List<T> list = new ArrayList<T>();
        list.add(o);
        return list;
    }

    public static <T> ArrayList<T> convertList(List<?> list) {
        ArrayList<T> newList = new ArrayList<T>();
        if(null == list) {
            return newList;
        }
        for (Object object : list) {
            try {
                newList.add((T)(object));
            } catch (Exception e) {

            }
        }
        return newList;
    }

    public static String[] toArrayOfString(List<?> validUrls) {
        if (validUrls == null) {
            return new String[] {};
        }
        String[] array = new String[validUrls.size()];
        for (int i = 0; i < validUrls.size(); i++) {
            array[i] = validUrls.get(i).toString();
        }
        return array;
    }

    public static <T> String[] toArrayOfString(T[] arrays) {
        if (arrays == null) {
            return new String[] {};
        }
        String[] array = new String[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            if (null == arrays[i]) {
                continue;
            }
            array[i] = arrays[i].toString();
        }
        return array;
    }

    public static boolean isCollection(Object o) {
        return Collection.class.isAssignableFrom(o.getClass());
    }

    public static boolean isCollection(Class<?> o) {
        return Collection.class.isAssignableFrom(o);
    }


    public static <T> boolean emptyArray(T[] arr) {
        return arr == null || arr.length == 0;
    }

    public static <T> Object[] toObjectArray(T[] rawArray) {

        Object[] resultArray = new Object[rawArray.length];

        for (int i = 0; i < rawArray.length; i++) {
            resultArray[i] = rawArray[i];
        }
        return resultArray;
    }


    static final List empty = new ArrayList<>();

    public static <T> List<T> emptyList() {

        return empty;
    }



    public static <T> List<T> reverse(List<T> arrayList) {
        List<T> reversedArrayList = new ArrayList<T>();
        for (int i = arrayList.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            reversedArrayList.add(arrayList.get(i));
        }

        // Return the reversed arraylist
        return reversedArrayList;
    }



}