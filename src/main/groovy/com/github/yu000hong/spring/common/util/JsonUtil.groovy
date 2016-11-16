package com.github.yu000hong.spring.common.util

import com.github.yu000hong.spring.common.common.Version
import com.google.gson.*

import java.lang.reflect.Type
import java.sql.Date
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat

public class JsonUtil {
    private static final JsonSerializer<Timestamp> TIMESTAMP_JSON_SERIALIZER = new JsonSerializer<Timestamp>() {
        @Override
        public JsonElement serialize(Timestamp value, Type type, JsonSerializationContext context) {
            return value == null ? null : new JsonPrimitive(value.getTime())
        }
    }
    private static final JsonDeserializer<Timestamp> TIMESTAMP_JSON_DESERIALIZER = new JsonDeserializer<Timestamp>() {
        @Override
        public Timestamp deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return json == null ? null : new Timestamp(json.getAsLong())
        }
    }
    private static final JsonSerializer<Date> DATE_JSON_SERIALIZER = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date value, Type type, JsonSerializationContext context) {
            return value == null ? null : new JsonPrimitive(DateUtil.dateFormatter.get().format(value))
        }
    }
    private static final JsonDeserializer<Date> DATE_JSON_DESERIALIZER = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            try {
                return json == null ? null : new Date(DateUtil.dateFormatter.get().parse(json.getAsString()).getTime())
            } catch (ParseException ignored) {
                throw new RuntimeException("invalid date format")
            }
        }
    }
    private static final JsonSerializer<Double> DOUBLE_JSON_SERIALIZER = new JsonSerializer<Double>() {
        @Override
        public JsonElement serialize(Double value, Type type, JsonSerializationContext context) {
            if (value == value.longValue()) {
                return new JsonPrimitive(value.longValue())
            }
            return new JsonPrimitive(value)
        }
    }
    private static final JsonSerializer<Version> VERSION_JSON_SERIALIZER = new JsonSerializer<Version>() {
        @Override
        JsonElement serialize(Version version, Type type, JsonSerializationContext jsonSerializationContext) {
            return version == null ? null : new JsonPrimitive(version.toString())
        }
    }

    private static Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, DATE_JSON_DESERIALIZER)
            .registerTypeAdapter(Date.class, DATE_JSON_SERIALIZER)
            .registerTypeAdapter(Timestamp.class, TIMESTAMP_JSON_DESERIALIZER)
            .registerTypeAdapter(Timestamp.class, TIMESTAMP_JSON_SERIALIZER)
            .registerTypeAdapter(Double.class, DOUBLE_JSON_SERIALIZER)
            .registerTypeAdapter(Version.class, VERSION_JSON_SERIALIZER)
            .disableHtmlEscaping()
            .create()

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz)
    }

    public static <T> T fromJson(String json, Type type) {
        return (T) gson.fromJson(json, type)
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj)
    }

    private static String getNumberStringValue(Map objectMap, String property) {
        Object obj = objectMap.get(property)
        if (obj == null) {
            return null
        }
        String value = String.valueOf(obj)
        if (value.trim().isEmpty()) {
            return null
        }
        return value
    }

    /*
     * parse long from json string value
     * return null when value is empty string or null
     * eg.
     * {
     *   "value": "3"
     * }
     */

    public static Boolean parseBoolean(Map objectMap, String property) {
        String value = getString(objectMap, property)
        if (value == null) {
            return null
        } else {
            return Boolean.valueOf(value)
        }
    }

    /*
     * get long value from json
     * return null when value is null
     * eg.
     * {
     *   "value": 3
     * }
     */

    public static Boolean getBoolean(Map objectMap, String property) {
        Object obj = objectMap.get(property)
        if (obj == null) {
            return null
        }
        return (Boolean) obj
    }

    /*
     * parse long from json string value
     * return null when value is empty string or null
     * eg.
     * {
     *   "value": "3"
     * }
     */

    public static Long parseLong(Map objectMap, String property) {
        String value = getNumberStringValue(objectMap, property)
        if (value == null) {
            return null
        } else {
            return Long.valueOf(value)
        }
    }

    /*
     * get long value from json
     * return null when value is null
     * eg.
     * {
     *   "value": 3
     * }
     */

    public static Long getLong(Map objectMap, String property) {
        Object obj = objectMap.get(property)
        if (obj == null) {
            return null
        }
        return ((Double) obj).longValue()
    }

    /*
     * parse int from json string value
     * return null when value is empty string or null
     * eg.
     * {
     *   "value": "3"
     * }
     */

    public static Integer parseInt(Map objectMap, String property) {
        String value = getNumberStringValue(objectMap, property)
        if (value == null) {
            return null
        } else {
            return Integer.valueOf(value)
        }
    }

    /*
     * get int value from json
     * return null when value is null
     * eg.
     * {
     *   "value": 3
     * }
     */

    public static Integer getInt(Map objectMap, String property) {
        Object obj = objectMap.get(property)
        if (obj == null) {
            return null
        }
        return (new Double(obj.toString())).intValue()
    }

    /*
     * parse double from json string value
     * return null when value is empty string or null
     * eg.
     * {
     *   "value": "3.1415"
     * }
     */

    public static Double parseDouble(Map objectMap, String property) {
        String value = getNumberStringValue(objectMap, property)
        if (value == null) {
            return null
        } else {
            return Double.valueOf(value)
        }
    }

    /*
     * get double value from json
     * return null when value is null
     * eg.
     * {
     *   "value": 3.1415
     * }
     */

    public static Double getDouble(Map objectMap, String property) {
        Object obj = objectMap.get(property)
        if (obj == null) {
            return null
        }
        return (Double) obj
    }

    public static Date parseDate(Map objectMap, String property) {
        Object value = objectMap.get(property)
        //默认时间视为null: '0000-00-00'
        if (value == null || '0000-00-00'.equals(value)) {
            return null
        }
        SimpleDateFormat dateFormat = DateUtil.dateFormatter.get()
        try {
            return new Date(dateFormat.parse(String.valueOf(value)).getTime())
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e)
        }
    }

    public static Timestamp parseTime(Map objectMap, String property) {
        Object value = objectMap.get(property)
        //默认时间视为null: '0000-00-00 00:00:00'
        if (value == null || '0000-00-00 00:00:00'.equals(value)) {
            return null
        }
        SimpleDateFormat datetimeFormat = DateUtil.timeFormatter.get()
        try {
            return new Timestamp(datetimeFormat.parse(String.valueOf(value)).getTime())
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e)
        }
    }

    public static String getString(Map objectMap, String property) {
        Object value = objectMap.get(property)
        if (value == null) {
            return null
        }
        return String.valueOf(value)
    }

    public static Map getMap(Map objectMap, String property) {
        Object value = objectMap.get(property)
        if (value == null) {
            return null
        }
        return (Map) value
    }
}
