/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.updateinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.Invoice;
import com.bitpay.demo.shared.StringToObjectParser;
import com.google.gson.reflect.TypeToken;
import java.util.Map;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

class GetInvoiceWithUpdateDataTest implements UnitTest {

    @Test
    void shouldCreateNewInvoiceWithUpdateData() throws ReflectiveOperationException, JSONException {
        // given
        final var invoiceJson = getDataFromFile("invoice.json");
        final var invoice = toObject(invoiceJson, Invoice.class);
        final var updateDataJson = getDataFromFile("updateData.json");
        final Map<String, Object> updateData = GSON.fromJson(
            updateDataJson,
            new TypeToken<Map<String, Object>>(){}.getType()
        );

        // when
        final var result = getTestedClass().execute(
            updateData,
            invoice
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("updatedInvoice.json"),
            new CustomComparator(
                JSONCompareMode.LENIENT,
                new Customization("createdDate", (o1, o2) -> true),
                new Customization("expirationTime", (o1, o2) -> true)
            )
        );
    }

    private GetInvoiceWithUpdateData getTestedClass() {
        return new GetInvoiceWithUpdateData(new StringToObjectParser());
    }
}
