/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.CurrencyCode;
import com.bitpay.demo.invoice.domain.payment.DisplaySubtotal;
import com.bitpay.demo.invoice.domain.payment.DisplayTotal;
import com.bitpay.demo.invoice.domain.payment.Enabled;
import com.bitpay.demo.invoice.domain.payment.FiatAmount;
import com.bitpay.demo.invoice.domain.payment.InvoicePayment;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrency;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrencyMinerFee;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrencySupportedTransactionCurrency;
import com.bitpay.demo.invoice.domain.payment.SatoshisPerByte;
import com.bitpay.demo.invoice.domain.payment.Subtotal;
import com.bitpay.demo.invoice.domain.payment.Total;
import com.bitpay.demo.invoice.domain.payment.TotalFee;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

class InvoicePaymentFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoicePayment() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final var result = getTestedClass().create(bitPayInvoice);

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoicePayment.json"),
            false
        );
    }

    private InvoicePaymentFactory getTestedClass() {
        return new InvoicePaymentFactory(getInvoicePaymentCurrencyFactory());
    }

    private InvoicePaymentCurrencyFactory getInvoicePaymentCurrencyFactory() {
        final var mock = Mockito.mock(InvoicePaymentCurrencyFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(new InvoicePaymentCurrency(
                Mockito.mock(InvoicePayment.class),
                new CurrencyCode(""),
                new Total(""),
                new Subtotal(""),
                new DisplayTotal(""),
                new DisplaySubtotal(""),
                new InvoicePaymentCurrencySupportedTransactionCurrency(
                    new Enabled(false),
                    null
                ),
                new InvoicePaymentCurrencyMinerFee(
                    new SatoshisPerByte(0.0),
                    new TotalFee(0.0),
                    new FiatAmount(0.0)
                )
            ));

        return mock;
    }
}
