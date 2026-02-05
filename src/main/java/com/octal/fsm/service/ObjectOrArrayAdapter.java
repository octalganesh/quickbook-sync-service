package com.octal.fsm.service;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ObjectOrArrayAdapter<T>
        implements JsonDeserializer<List<T>> {

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext ctx) {

        Type itemType =
                ((java.lang.reflect.ParameterizedType) typeOfT)
                        .getActualTypeArguments()[0];

        List<T> list = new ArrayList<>();

        if (json == null || json.isJsonNull()) {
            return list;
        }

        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                list.add(ctx.deserialize(e, itemType));
            }
        } else if (json.isJsonObject()) {
            list.add(ctx.deserialize(json, itemType));
        }

        return list;
    }
}
