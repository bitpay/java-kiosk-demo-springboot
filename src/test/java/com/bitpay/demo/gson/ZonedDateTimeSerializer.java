/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public class ZonedDateTimeSerializer implements JsonSerializer<ZonedDateTime> {

    @Override
    public JsonElement serialize(
        final ZonedDateTime zonedDateTime,
        final Type type,
        final JsonSerializationContext jsonSerializationContext
    ) {
        if (zonedDateTime == null) {
            return new Gson().toJsonTree(zonedDateTime);
        }

        return new JsonPrimitive(zonedDateTime.toString());
    }
}
