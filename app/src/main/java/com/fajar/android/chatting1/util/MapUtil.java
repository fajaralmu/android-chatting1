package com.fajar.android.chatting1.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapUtil {
    public static <T> List<T> convertMapList(List<Map<?, ?>> mapList, Class<T> objectClass) {

        List<T> result = new ArrayList<T>();

        for (Object object : mapList) {
            result.add(mapToObject((Map<?, ?>) object, objectClass));
        }

        return result;
    }

    /*
     * public static void printMap(Map map) { Log.log("printing map"); for(Object
     * key:map.keySet()) { Log.log("key:", key,":",map.get(key)); } }
     */

    public static <T> T mapToObject(Map<?, ?> map, Class<T> objectClass) {
        Set<?> mapKeys = map.keySet();
        try {
            Object result = objectClass.newInstance();

            for (Object key : mapKeys) {
                Object value = map.get(key);
                Field field = getDeclaredField(objectClass, key.toString());

                try {

                    if (value != null && field != null) {

                        Class<?> fieldType = field.getType();
                        boolean isEnum = fieldType.isEnum();

                        /**
                         * mapValue is map
                         */
                        if (value.getClass().equals(Map.class) || value.getClass().equals(LinkedHashMap.class)) {

                            value = mapToObject((Map) value, fieldType);
                        } else
                        /**
                         * long
                         */
                            if (objectEquals(fieldType, long.class, Long.class)) {
                                value = Long.valueOf(value.toString());
                            } else
                            /**
                             * int
                             */
                                if (objectEquals(fieldType, int.class, Integer.class)) {
                                    value = Integer.parseInt(value.toString());
                                } else
                                /**
                                 * double
                                 */
                                    if (objectEquals(fieldType, double.class, Double.class)) {
                                        value = Double.valueOf(value.toString());
                                    } else
                                    /**
                                     * date from Long
                                     */
                                        if (fieldType.equals(Date.class) && (objectEquals(value.getClass(), Long.class, long.class))) {
                                            value = new Date((Long) value);
                                        } else
                                        if (fieldType.equals(Date.class) && (objectEquals(value.getClass(), String.class))) {
                                            //String date = "17-11-2020 01:01:19";
                                            String pattern = "dd-MM-yyyy HH:mm:ss";
                                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                                            value =  sdf.parse(String.valueOf(value));
                                        } else
                                        /**
                                         * long from date
                                         */
                                            if (objectEquals(fieldType, long.class, Long.class) && value.getClass().equals(Date.class)) {
                                                value = ((Date) value).getTime();
                                            } else
                                            /**
                                             * ENUM
                                             */
                                                if (isEnum) {
                                                    value = Enum.valueOf((Class) fieldType, value.toString());
                                                }

                        field.setAccessible(true);
                        field.set(result, value);
                    }
                } catch (Exception e) {
                    String valueType = value == null ? "NULL" : value.getClass().toString();

                    continue;
                }
            }

            return (T) result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }

    }

    public static boolean objectEquals(Object object, Object... objects) {

        for (Object object2 : objects) {
            if (object.equals(object2)) {
                return true;
            }
        }

        return false;
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field == null) {

            }
            field.setAccessible(true);
            return field;

        } catch (Exception e) {
            //log.error("Error get declared field in the class, and try access super class");
        }
        if (clazz.getSuperclass() != null) {

            try {
                //log.info("TRY ACCESS SUPERCLASS");

                Field superClassField = clazz.getSuperclass().getDeclaredField(fieldName);
                superClassField.setAccessible(true);
                return superClassField;
            } catch (Exception e) {

               // e.printStackTrace();
            }
        }

        return null;
    }
}
