/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.CurrencyCode;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefund;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefundInfo;
import com.bitpay.demo.invoice.domain.refund.SupportRequest;
import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoiceRefundFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoiceRefund() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final var result = getTestedClass().create(bitPayInvoice);

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceRefund.json"),
            false
        );
    }

    private InvoiceRefundFactory getTestedClass() {
        return new InvoiceRefundFactory(
            object -> new Gson().toJson(object),
            getInvoiceRefundInfoFactory()
        );
    }

    private InvoiceRefundInfoFactory getInvoiceRefundInfoFactory() {
        final var mock = Mockito.mock(InvoiceRefundInfoFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(new InvoiceRefundInfo(
                Mockito.mock(InvoiceRefund.class),
                new SupportRequest(""),
                new CurrencyCode("")
            ));

        return mock;
    }
}
