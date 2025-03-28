/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.CurrencyCode;
import com.bitpay.demo.invoice.domain.refund.Amount;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefund;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefundInfo;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefundInfoAmount;
import com.bitpay.demo.invoice.domain.refund.SupportRequest;
import com.bitpay.sdk.model.invoice.RefundInfo;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;

@DependencyInjection
class InvoiceRefundInfoFactory {

    @NonNull
    public InvoiceRefundInfo create(
        @NonNull final InvoiceRefund invoiceRefund,
        @NonNull final RefundInfo info
    ) {
        final var invoiceRefundInfo = new InvoiceRefundInfo(
            invoiceRefund,
            new SupportRequest(info.getSupportRequest()),
            new CurrencyCode(info.getCurrency())
        );

        invoiceRefundInfo.addRefundInfoAmounts(
            getRefundInfoAmounts(
                invoiceRefundInfo,
                info.getAmounts()
            )
        );

        return invoiceRefundInfo;
    }

    @NonNull
    private Collection<InvoiceRefundInfoAmount> getRefundInfoAmounts(
        @NonNull final InvoiceRefundInfo invoiceRefundInfo,
        @NonNull final Map<String, Double> amounts
    ) {
        return amounts.entrySet().stream()
            .map(amount -> new InvoiceRefundInfoAmount(
                invoiceRefundInfo,
                new CurrencyCode(amount.getKey()),
                new Amount(amount.getValue())
            ))
            .collect(Collectors.toList());
    }
}
