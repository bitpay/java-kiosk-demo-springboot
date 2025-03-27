/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.Invoice;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoiceTransactionFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoiceTransaction() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final var result = getTestedClass().create(
            Mockito.mock(Invoice.class),
            bitPayInvoice.getTransactions().get(0)
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceTransaction.json"),
            false
        );
    }

    private InvoiceTransactionFactory getTestedClass() {
        return new InvoiceTransactionFactory();
    }
}
