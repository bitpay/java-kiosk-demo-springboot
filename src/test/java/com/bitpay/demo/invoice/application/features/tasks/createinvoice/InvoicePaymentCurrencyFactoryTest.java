/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.payment.InvoicePayment;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoicePaymentCurrencyFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoicePaymentCurrency() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final var result = getTestedClass().create(
            bitPayInvoice.getPaymentTotals().entrySet().stream().findFirst().get(),
            new InvoicePayment(null, null, null, null, null, null, null, null),
            bitPayInvoice
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoicePaymentCurrency.json"),
            false
        );
    }

    private InvoicePaymentCurrencyFactory getTestedClass() {
        return new InvoicePaymentCurrencyFactory();
    }
}
