/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefund;
import com.bitpay.demo.invoice.domain.refund.InvoiceRefundInfo;
import com.bitpay.demo.invoice.domain.refund.RefundAddressRequestPending;
import com.bitpay.demo.invoice.domain.refund.RefundAddressesJson;
import com.bitpay.demo.shared.ObjectToJsonConverter;
import com.bitpay.sdk.model.invoice.Invoice;
import com.bitpay.sdk.model.invoice.RefundInfo;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.NonNull;

@DependencyInjection
class InvoiceRefundFactory {

    private final ObjectToJsonConverter objectToJsonConverter;
    private final InvoiceRefundInfoFactory invoiceRefundInfoFactory;

    InvoiceRefundFactory(
        @NonNull final ObjectToJsonConverter objectToJsonConverter,
        @NonNull final InvoiceRefundInfoFactory invoiceRefundInfoFactory
    ) {
        this.objectToJsonConverter = objectToJsonConverter;
        this.invoiceRefundInfoFactory = invoiceRefundInfoFactory;
    }

    @NonNull
    public InvoiceRefund create(@NonNull final Invoice bitPayInvoice) {
        final var invoiceRefund = new InvoiceRefund(
            new RefundAddressesJson(this.objectToJsonConverter.execute(bitPayInvoice.getRefundAddresses())),
            new RefundAddressRequestPending(bitPayInvoice.getRefundAddressRequestPending())
        );

        invoiceRefund.addRefundInfo(
            getRefundInfo(
                invoiceRefund,
                bitPayInvoice.getRefundInfo()
            )
        );

        return invoiceRefund;
    }

    @NonNull
    private Collection<InvoiceRefundInfo> getRefundInfo(
        @NonNull final InvoiceRefund invoiceRefund,
        @Nullable final Collection<RefundInfo> refundInfo
    ) {
        if (Objects.isNull(refundInfo)) {
            return List.of();
        }

        return refundInfo.stream()
            .map(info -> this.invoiceRefundInfoFactory.create(invoiceRefund, info))
            .collect(Collectors.toList());
    }
}
