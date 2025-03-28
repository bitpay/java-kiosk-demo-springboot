/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.Invoice;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoiceItemizedDetailFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoiceItemizedDetail() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();
        final var invoiceJson = getDataFromFile("invoice.json");
        final var invoice = toObject(invoiceJson, Invoice.class);

        // when
        final var result = getTestedClass().create(
            invoice,
            bitPayInvoice.getItemizedDetails().get(0)
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceItemizedDetail.json"),
            false
        );
    }

    private InvoiceItemizedDetailFactory getTestedClass() {
        return new InvoiceItemizedDetailFactory();
    }
}
