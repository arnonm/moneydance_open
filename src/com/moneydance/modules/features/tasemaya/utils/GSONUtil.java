package com.moneydance.modules.features.tasemaya.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.moneydance.modules.features.tasemaya.adapters.FundsLocalDateAdapter;
import com.moneydance.modules.features.tasemaya.adapters.FundsLocalDateTimeAdapter;

public final class GSONUtil {

   

    private GSONUtil() {
    }

    public static Gson createGson() {

        // @formatter:off
        return new GsonBuilder()
                // .registerTypeAdapter(Map.class, createMapDeserializer())
                // .registerTypeAdapter(List.class, createListDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new FundsLocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new FundsLocalDateAdapter())
                .create();

        // @formatter:on
    }


    private static JsonDeserializer<Map<String, Object>> createMapDeserializer() {
        return new JsonDeserializer<Map<String, Object>>() {

            @Override
            public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {

                return json.getAsJsonObject().entrySet().stream() // stream
                        .collect(Collectors.toMap(Entry::getKey, (e) -> GSONUtil.deserialize(e.getValue(), context)));
            }
        };
    }

    private static JsonDeserializer<List<Object>> createListDeserializer() {
        return new JsonDeserializer<List<Object>>() {

            @Override
            public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {

                return StreamSupport.stream(json.getAsJsonArray().spliterator(), false) // stream
                        .map((e) -> GSONUtil.deserialize(e, context)).collect(Collectors.toList());
            }
        };
    }

    private static Object deserialize(JsonElement value, JsonDeserializationContext context) {

        if (value.isJsonNull()) {
            return null;
        }
        if (value.isJsonObject()) {
            return context.deserialize(value, Map.class);
        }
        if (value.isJsonArray()) {
            return context.deserialize(value, List.class);
        }
        if (value.isJsonPrimitive()) {
            return parsePrimitive(value);
        }

        throw new IllegalStateException("This exception should never be thrown!");
    }

    private static Object parsePrimitive(JsonElement value) {

        final JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();


        if ( jsonPrimitive.isString() && jsonPrimitive.getAsString().isEmpty() ) {
            return null;
        }

        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }

        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }

        if (jsonPrimitive.isNumber()) {
            return parseNumber(jsonPrimitive);
        }

        throw new IllegalStateException("This exception should never be thrown!");
    }

    private static Number parseNumber(JsonPrimitive jsonPrimitive) {

        if (isInteger(jsonPrimitive)) {
            return jsonPrimitive.getAsLong();
        }

        return jsonPrimitive.getAsDouble();
    }

    private static boolean isInteger(final JsonPrimitive jsonPrimitive) {
        return jsonPrimitive.getAsString().matches("[-]?\\d+");
    }
}