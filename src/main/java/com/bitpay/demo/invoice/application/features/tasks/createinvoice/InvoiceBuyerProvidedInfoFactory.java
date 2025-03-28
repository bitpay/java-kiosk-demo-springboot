/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.buyer.BuyerEmailAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerName;
import com.bitpay.demo.invoice.domain.buyer.BuyerPhoneNumber;
import com.bitpay.demo.invoice.domain.buyer.BuyerSelectedTransactionCurrency;
import com.bitpay.demo.invoice.domain.buyer.BuyerSelectedWallet;
import com.bitpay.demo.invoice.domain.buyer.BuyerSms;
import com.bitpay.demo.invoice.domain.buyer.BuyerSmsVerified;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyerProvidedInfo;
import lombok.NonNull;

@DependencyInjection
class InvoiceBuyerProvidedInfoFactory {

    @NonNull
    public InvoiceBuyerProvidedInfo create(
        @NonNull final com.bitpay.sdk.model.invoice.InvoiceBuyerProvidedInfo invoiceBuyerProvidedInfo
    ) {
        return new InvoiceBuyerProvidedInfo(
            new BuyerName(invoiceBuyerProvidedInfo.getName()),
            new BuyerPhoneNumber(invoiceBuyerProvidedInfo.getPhoneNumber()),
            new BuyerSelectedTransactionCurrency(invoiceBuyerProvidedInfo.getSelectedTransactionCurrency()),
            new BuyerEmailAddress(invoiceBuyerProvidedInfo.getEmailAddress()),
            new BuyerSelectedWallet(invoiceBuyerProvidedInfo.getSelectedWallet()),
            new BuyerSms(invoiceBuyerProvidedInfo.getSms()),
            new BuyerSmsVerified(invoiceBuyerProvidedInfo.getSmsVerified())
        );
    }
}
