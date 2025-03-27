/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.demo.invoice.domain.Amount;
import com.bitpay.demo.invoice.domain.Invoice;
import com.bitpay.demo.invoice.domain.InvoiceUuid;
import com.bitpay.demo.invoice.domain.buyer.BuyerEmailAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerName;
import com.bitpay.demo.invoice.domain.buyer.BuyerPhoneNumber;
import com.bitpay.demo.invoice.domain.buyer.BuyerSelectedTransactionCurrency;
import com.bitpay.demo.invoice.domain.buyer.BuyerSelectedWallet;
import com.bitpay.demo.invoice.domain.buyer.BuyerSms;
import com.bitpay.demo.invoice.domain.buyer.BuyerSmsVerified;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyer;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyerProvidedInfo;
import com.bitpay.demo.invoice.domain.itemizeddetail.Description;
import com.bitpay.demo.invoice.domain.itemizeddetail.InvoiceItemizedDetail;
import com.bitpay.demo.invoice.domain.itemizeddetail.IsFee;
import com.bitpay.demo.invoice.domain.payment.InvoicePayment;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefund;
import com.bitpay.demo.invoice.domain.refund.RefundAddressRequestPending;
import com.bitpay.demo.invoice.domain.refund.RefundAddressesJson;
import com.bitpay.demo.invoice.domain.transaction.InvoiceTransaction;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

class InvoiceFactoryTest implements UnitTest, GetBitPayInvoice {

    @Test
    void shouldMapToInvoice() throws JSONException {
        // given
        final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice = getBitPayInvoice();

        // when
        final var result = getTestedClass().create(bitPayInvoice, new InvoiceUuid("123-456-789"));

        // then
        JSONAssert.assertEquals(
            toJson(result),
            getDataFromFile("invoice.json"),
            new CustomComparator(
                JSONCompareMode.LENIENT,
                new Customization("createdDate", (o1, o2) -> true),
                new Customization("expirationTime", (o1, o2) -> true)
            )
        );
    }

    private InvoiceFactory getTestedClass() {
        return new InvoiceFactory(
            getInvoicePaymentFactory(),
            getInvoiceBuyerFactory(),
            getInvoiceRefundFactory(),
            getInvoiceTransactionFactory(),
            getInvoiceItemizedDetailFactory()
        );
    }

    private static InvoiceTransactionFactory getInvoiceTransactionFactory() {
        final var mock = Mockito.mock(InvoiceTransactionFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
            new InvoiceTransaction(
                Mockito.mock(Invoice.class),
                new Amount(0.0),
                null,
                null,
                null
            )
        );

        return mock;
    }

    private static InvoiceItemizedDetailFactory getInvoiceItemizedDetailFactory() {
        final var mock = Mockito.mock(InvoiceItemizedDetailFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
            new InvoiceItemizedDetail(
                Mockito.mock(Invoice.class),
                new Amount(0.0),
                new Description(""),
                new IsFee(false)
            )
        );

        return mock;
    }

    private InvoiceRefundFactory getInvoiceRefundFactory() {
        final var mock = Mockito.mock(InvoiceRefundFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any())).thenReturn(
            new InvoiceRefund(
                new RefundAddressesJson(""),
                new RefundAddressRequestPending(false)
            )
        );

        return mock;
    }

    private InvoiceBuyerFactory getInvoiceBuyerFactory() {
        final var mock = Mockito.mock(InvoiceBuyerFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
            new InvoiceBuyer(
                new InvoiceBuyerProvidedInfo(
                    new BuyerName(""),
                    new BuyerPhoneNumber(""),
                    new BuyerSelectedTransactionCurrency(""),
                    new BuyerEmailAddress(""),
                    new BuyerSelectedWallet(""),
                    new BuyerSms(""),
                    new BuyerSmsVerified(false)
                )
            )
        );

        return mock;
    }

    private InvoicePaymentFactory getInvoicePaymentFactory() {
        final var mock = Mockito.mock(InvoicePaymentFactory.class);
        Mockito.when(mock.create(ArgumentMatchers.any())).thenReturn(new InvoicePayment());

        return mock;
    }
}
