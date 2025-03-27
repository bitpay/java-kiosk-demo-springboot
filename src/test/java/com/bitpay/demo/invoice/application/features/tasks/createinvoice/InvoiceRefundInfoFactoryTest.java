/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefund;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoiceRefundInfoFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoiceRefundInfo() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final var result = getTestedClass().create(
            Mockito.mock(InvoiceRefund.class),
            bitPayInvoice.getRefundInfo().get(0)
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceRefundInfo.json"),
            false
        );
    }

    private InvoiceRefundInfoFactory getTestedClass() {
        return new InvoiceRefundInfoFactory();
    }
}
