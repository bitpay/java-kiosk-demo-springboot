/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.updateinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.BitPayId;
import com.bitpay.demo.shared.StringToObjectParser;
import com.google.gson.reflect.TypeToken;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateUpdateDataTest implements UnitTest {

    @Test
    void shouldPassValidation() {
        // given
        final var updateDataJson = getDataFromFile("updateData.json");
        final Map<String, Object> updateData = GSON.fromJson(
            updateDataJson, 
            new TypeToken<Map<String, Object>>(){}.getType()
        );

        // when
        getTestedClass().execute(
            updateData,
            new BitPayId("A8Eo7WppQBpQjcgC2mKXqV")
        );

        // then
        // Do not throw any error
    }

    @Test
    void shouldNotPassValidation() {
        // given
        final var updateDataJson = getDataFromFile("invalidUpdateData.json");
        final Map<String, Object> updateData = GSON.fromJson(
            updateDataJson, 
            new TypeToken<Map<String, Object>>(){}.getType()
        );

        try {
            // when
            getTestedClass().execute(
                updateData,
                new BitPayId("A8Eo7WppQBpQjcgC2mKXqV2")
            );
        } catch (final ValidationInvoiceUpdateDataFailed exception) {
            // then
            Assertions.assertEquals(19, exception.getErrors().size());
        }

        // Do not throw any error
    }

    private ValidateUpdateData getTestedClass() {
        return new ValidateUpdateData(new StringToObjectParser());
    }
}
