/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.Amount;
import com.bitpay.demo.invoice.domain.Invoice;
import com.bitpay.demo.invoice.domain.itemizeddetail.Description;
import com.bitpay.demo.invoice.domain.itemizeddetail.InvoiceItemizedDetail;
import com.bitpay.demo.invoice.domain.itemizeddetail.IsFee;
import com.bitpay.sdk.model.invoice.InvoiceItemizedDetails;
import lombok.NonNull;

@DependencyInjection
class InvoiceItemizedDetailFactory {
    @NonNull
    public InvoiceItemizedDetail create(
        @NonNull final Invoice invoice,
        @NonNull final InvoiceItemizedDetails itemizedDetail
    ) {
        return new InvoiceItemizedDetail(
            invoice,
            new Amount(itemizedDetail.getAmount()),
            new Description(itemizedDetail.getDescription()),
            new IsFee(itemizedDetail.getIsFee())
        );
    }
}
