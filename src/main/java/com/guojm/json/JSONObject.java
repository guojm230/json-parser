package com.guojm.json;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

public class JSONObject extends LinkedHashMap<String,Object> {

    public int getInt(String key){
        var number = get(key);
        if (number == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (number instanceof Number num){
            return num.intValue();
        }
        throw new NoSuchElementException("doesn't exist int value for key:"+key);
    }

    public Long getLong(String key){
        var number = get(key);
        if (number == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (number instanceof Number num){
            return num.longValue();
        }
        throw new NoSuchElementException("doesn't exist long value for key:"+key);
    }

    public double getDouble(String key){
        var number = get(key);
        if (number == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (number instanceof Number num){
            return num.doubleValue();
        }
        throw new NoSuchElementException("doesn't exist double value for key:"+key);
    }

    public float getFloat(String key){
        var number = get(key);
        if (number == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (number instanceof Number num){
            return num.floatValue();
        }
        throw new NoSuchElementException("doesn't exist float value for key:"+key);
    }

    public String getString(String key){
        var ele = get(key);
        if (ele == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (ele instanceof String str){
            return str;
        }
        throw new NoSuchElementException("doesn't exist string value for key:"+key);
    }

    public boolean getBoolean(String key){
        var ele = get(key);
        if (ele == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (ele instanceof Boolean bool){
            return bool;
        }
        throw new NoSuchElementException("doesn't exist boolean value for key:"+key);
    }

    public JSONObject getJSONObject(String key){
        var ele = get(key);
        if (ele == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (ele instanceof JSONObject jsonObject){
            return jsonObject;
        }
        throw new NoSuchElementException("doesn't exist JSONObject value for key:"+key);
    }

    public JSONArray getJSONOArray(String key){
        var ele = get(key);
        if (ele == null){
            throw new NoSuchElementException("doesn't exist key:"+key);
        }
        if (ele instanceof JSONArray jsonArray){
            return jsonArray;
        }
        throw new NoSuchElementException("doesn't exist JSONObject value for key:"+key);
    }
}
