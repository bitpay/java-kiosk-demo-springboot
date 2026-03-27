/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo;

import com.bitpay.demo.gson.ExcludeFields;
import com.bitpay.demo.gson.LocalDateDeserializer;
import com.bitpay.demo.gson.LocalDateSerializer;
import com.bitpay.demo.gson.LocalDateTimeDeserializer;
import com.bitpay.demo.gson.LocalDateTimeSerializer;
import com.bitpay.demo.gson.ZonedDateTimeDeserializer;
import com.bitpay.demo.gson.ZonedDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public interface UnitTest {

    Gson GSON = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
        .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer())
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .setExclusionStrategies(new ExcludeFields())
        .create();

    default String toJson(final Object object) {
        return GSON.toJson(object);
    }

    default <T> T toObject(
        final String json,
        final Class<T> type
    ) {
        return GSON.fromJson(json, type);
    }

    default String getDataFromFile(final String fileName) {
        final String pathname = "src/test/java/" + getClass().getPackageName()
            .replaceAll("\\.", "/") + "/" + fileName;

        try {
            return FileUtils.readFileToString(new File(pathname), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}

