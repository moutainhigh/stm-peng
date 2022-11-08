package com.mainsteam.stm.dsl.expression;

import java.util.Map;

public interface OCELContext extends Map<String, Object> {

    Object get(String key);

    boolean has(String key);

    void set(String key, Object value);
}
