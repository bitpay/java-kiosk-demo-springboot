/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.Amount;
import com.bitpay.demo.invoice.domain.Invoice;
import com.bitpay.demo.invoice.domain.transaction.Confirmations;
import com.bitpay.demo.invoice.domain.transaction.Txid;
import com.bitpay.sdk.model.invoice.InvoiceTransaction;
import java.time.ZoneId;
import java.util.Objects;
import lombok.NonNull;

@DependencyInjection
class InvoiceTransactionFactory {

    @NonNull
    public com.bitpay.demo.invoice.domain.transaction.InvoiceTransaction create(
        @NonNull final Invoice invoice,
        @NonNull final InvoiceTransaction transaction
    ) {
        return new com.bitpay.demo.invoice.domain.transaction.InvoiceTransaction(
            invoice,
            new Amount(transaction.getAmount().doubleValue()),
            new Confirmations(transaction.getConfirmations()),
            Objects.nonNull(transaction.getReceivedTime())
                ? transaction.getReceivedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null,
            new Txid(transaction.getTransactionId())
        );
    }
}
