/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoiceBuyerProvidedInfoFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoiceBuyerProvidedInfo() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final com.bitpay.demo.invoice.domain.buyer.InvoiceBuyerProvidedInfo result = getTestedClass()
            .create(bitPayInvoice.getInvoiceBuyerProvidedInfo());

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceBuyerProvidedInfo.json"),
            false
        );
    }

    private InvoiceBuyerProvidedInfoFactory getTestedClass() {
        return new InvoiceBuyerProvidedInfoFactory();
    }
}
