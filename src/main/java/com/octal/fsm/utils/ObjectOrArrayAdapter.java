package com.octal.fsm.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ObjectOrArrayAdapter<T> implements JsonDeserializer<List<T>> {

    private Class<T> clazz;

    public ObjectOrArrayAdapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        List<T> list = new ArrayList<>();

        if (json.isJsonNull()) {
            return list;
        }

        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element ->
                    list.add(context.deserialize(element, clazz)));
            return list;
        }

        // single object case
        list.add(context.deserialize(json, clazz));
        return list;
    }
}
