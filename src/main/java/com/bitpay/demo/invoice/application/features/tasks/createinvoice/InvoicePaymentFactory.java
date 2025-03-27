/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.payment.AmountPaid;
import com.bitpay.demo.invoice.domain.payment.DisplayAmountPaid;
import com.bitpay.demo.invoice.domain.payment.InvoicePayment;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrency;
import com.bitpay.demo.invoice.domain.payment.NonPayProPaymentReceived;
import com.bitpay.demo.invoice.domain.payment.OverpaidAmount;
import com.bitpay.demo.invoice.domain.payment.TransactionCurrency;
import com.bitpay.demo.invoice.domain.payment.UnderpaidAmount;
import com.bitpay.demo.invoice.domain.payment.UniversalCodesPaymentString;
import com.bitpay.demo.invoice.domain.payment.UniversalCodesVerificationLink;
import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;

@DependencyInjection
class InvoicePaymentFactory {

    private final InvoicePaymentCurrencyFactory invoicePaymentCurrencyFactory;

    InvoicePaymentFactory(@NonNull final InvoicePaymentCurrencyFactory invoicePaymentCurrencyFactory) {
        this.invoicePaymentCurrencyFactory = invoicePaymentCurrencyFactory;
    }

    @NonNull
    InvoicePayment create(@NonNull final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice) {
        final var invoicePayment = new InvoicePayment(
            new AmountPaid(bitPayInvoice.getAmountPaid().doubleValue()),
            new DisplayAmountPaid(bitPayInvoice.getDisplayAmountPaid().doubleValue()),
            new NonPayProPaymentReceived(bitPayInvoice.getNonPayProPaymentReceived()),
            new UniversalCodesPaymentString(bitPayInvoice.getUniversalCodes().getBitpay()),
            new UniversalCodesVerificationLink(bitPayInvoice.getUniversalCodes().getVerificationLink()),
            new TransactionCurrency(bitPayInvoice.getTransactionCurrency()),
            Optional.ofNullable(bitPayInvoice.getUnderPaidAmount())
                .map(amount -> new UnderpaidAmount(amount.doubleValue()))
                .orElse(null),
            Optional.ofNullable(bitPayInvoice.getOverPaidAmount())
                .map(amount -> new OverpaidAmount(amount.doubleValue()))
                .orElse(null)
        );

        invoicePayment.addPaymentCurrencies(
            getInvoicePaymentCurrencies(
                invoicePayment,
                bitPayInvoice
            )
        );

        return invoicePayment;
    }

    @NonNull
    private Collection<InvoicePaymentCurrency> getInvoicePaymentCurrencies(
        @NonNull final InvoicePayment invoicePayment,
        @NonNull final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice
    ) {
        return bitPayInvoice.getPaymentTotals().entrySet().stream()
            .map(code -> this.invoicePaymentCurrencyFactory.create(code, invoicePayment, bitPayInvoice))
            .toList();
    }
}
