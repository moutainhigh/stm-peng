package com.mainsteam.stm.dsl.expression;

import java.util.HashMap;

public class MapContext extends HashMap<String, Object> implements OCELContext {

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    @Override
    public boolean has(String key) {
        return super.containsKey(key);
    }

    @Override
    public void set(String key, Object value) {
        super.put(key, value);
    }
}
