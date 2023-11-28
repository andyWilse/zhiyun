package com.religion.zhiyun.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: rayfoo
 * @create: 2020-06-25 17:20
 **/
public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * Object转JSONString
     */
    public static String objectTOJSONString(Object object) {
        String string = JSON.toJSONString(object);
        return string;
    }

    /**
     * 对象转换成json字符串
     */
    public static String beanToJson(Object object) {
        if (object == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * Map转成json
     */
    public static String mapToJson(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map, Map.class);
    }

    /**
     * Map转成json
     */
    public static String maparrToJson(Map<String, String[]> map) {
        Gson gson = new Gson();
        Map <String,String> smap=new HashMap<>();
        Set<String> set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            smap.put((String) next,map.get(next)[0]);
        }
        return gson.toJson(smap, Map.class);
    }

    /**
     * json转成map
     */
    public static Map<String, Object> jsonToMap(String jsonString) {
        Gson gson = new Gson();
        Map<String, Object> stringHashMap = new HashMap<String, Object>();
        Map map = gson.fromJson(jsonString, stringHashMap.getClass());
        return map;
    }

    /**
     * jsonString 转 JsonObject
     */
    public static JsonObject jsonToJsonObject(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        JsonObject json = gson.fromJson(jsonString, jsonObject.getClass());
        return json;
    }

    /**
     * JsonObject转LinkedHashMap
     */
    public static LinkedHashMap<String, String> jsonToMapJsonObject(JsonObject json) {
        Gson gson = new Gson();
        LinkedHashMap<String, String> stringHashMap = new LinkedHashMap<String, String>();
        LinkedHashMap map = gson.fromJson(json, stringHashMap.getClass());
        return map;
    }



    /**
     * 实体类对象 转 Map
     */
    public static Map<String, Object> objectMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * Object 转List<实体>
     */
    public static <T> List<T> castList(Object object, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (object instanceof List<?>) {
            for (Object o : (List<?>) object) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * json字符串转换成对象
     */
    public static <T> T jsonTOBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * json字符串转换成List集合 (需要实体类)
     */
    public static <T> List<T> jsonTOList(String jsonString, Class cls) {
        List<T> list = null;
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * List集合转换成json字符串
     */
    public static String listTOJson(Object obj) {
        return JSONArray.toJSONString(obj, true);
    }

    /**
     * json转List (不需要实体类)
     */
    public static JSONArray jsonTOList(String jsonStr) {
        return JSON.parseArray(jsonStr);
    }

    public static void jsonTOBean(String sysJson) {
    }

    /**
     * 数组转JSON
     */
    public static String arrToJSON(Object[] object) {
        // 创建一个新的Gson实例
        Gson gson = new Gson();
        String string =gson.toJson(object);
        return string;
    }

    /**
     * json转String数组
     */
    public static String[] JSONToStringArr(String json) {
        // 创建一个新的Gson实例
        Gson gson = new Gson();
        String[] str = gson.fromJson(json, String[].class);
        return str;
    }

    /**
     * json转INT数组
     */
    public static int[] JSONToIntArr(String json) {
        // 创建一个新的Gson实例
        Gson gson = new Gson();
        int[] str = gson.fromJson(json, int[].class);
        return str;
    }


}

