/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.buyer.BuyerEmailAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerName;
import com.bitpay.demo.invoice.domain.buyer.BuyerPhoneNumber;
import com.bitpay.demo.invoice.domain.buyer.BuyerSelectedTransactionCurrency;
import com.bitpay.demo.invoice.domain.buyer.BuyerSelectedWallet;
import com.bitpay.demo.invoice.domain.buyer.BuyerSms;
import com.bitpay.demo.invoice.domain.buyer.BuyerSmsVerified;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyer;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyerProvidedInfo;
import com.bitpay.sdk.model.invoice.Invoice;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoiceBuyerFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoiceBuyer() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final InvoiceBuyer result = getTestedClass().create(
            bitPayInvoice,
            Mockito.mock(com.bitpay.sdk.model.invoice.InvoiceBuyerProvidedInfo.class)
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceBuyer.json"),
            false
        );
    }

    @Test
    void shouldMapToInvoiceBuyerWhenBitPayHasNotBuyer() throws JSONException {
        // given
        final String bitPayInvoiceJson = getDataFromFile("bitPayInvoiceWithoutBuyer.json");
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = toObject(bitPayInvoiceJson, Invoice.class);

        // when
        final InvoiceBuyer result = getTestedClass().create(
            bitPayInvoice,
            Mockito.mock(com.bitpay.sdk.model.invoice.InvoiceBuyerProvidedInfo.class)
        );

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoiceBuyerWithoutBitPayBuyer.json"),
            false
        );
    }

    private InvoiceBuyerFactory getTestedClass() {
        final var mock = Mockito.mock(InvoiceBuyerProvidedInfoFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any())).thenReturn(
            new InvoiceBuyerProvidedInfo(
                new BuyerName(""),
                new BuyerPhoneNumber(""),
                new BuyerSelectedTransactionCurrency(""),
                new BuyerEmailAddress(""),
                new BuyerSelectedWallet(""),
                new BuyerSms(""),
                new BuyerSmsVerified(false)
            )
        );

        return new InvoiceBuyerFactory(mock);
    }
}
