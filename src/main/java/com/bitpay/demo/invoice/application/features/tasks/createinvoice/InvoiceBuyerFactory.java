/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.buyer.BuyerAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerCity;
import com.bitpay.demo.invoice.domain.buyer.BuyerCountry;
import com.bitpay.demo.invoice.domain.buyer.BuyerEmailAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerName;
import com.bitpay.demo.invoice.domain.buyer.BuyerNotify;
import com.bitpay.demo.invoice.domain.buyer.BuyerPhoneNumber;
import com.bitpay.demo.invoice.domain.buyer.BuyerPostalCode;
import com.bitpay.demo.invoice.domain.buyer.BuyerProvidedEmail;
import com.bitpay.demo.invoice.domain.buyer.BuyerRegion;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyer;
import com.bitpay.sdk.model.Invoice.Invoice;
import java.util.Objects;
import lombok.NonNull;

@DependencyInjection
class InvoiceBuyerFactory {

    @NonNull
    public InvoiceBuyer create(@NonNull final Invoice bitPayInvoice) {
        final var buyer = bitPayInvoice.getBuyer();

        if (Objects.isNull(buyer)) {
            return new InvoiceBuyer(
                new BuyerProvidedEmail(bitPayInvoice.getBuyerProvidedEmail())
            );
        }

        return new InvoiceBuyer(
            new BuyerName(buyer.getName()),
            new BuyerAddress(buyer.getAddress1()),
            new BuyerAddress(buyer.getAddress2()),
            new BuyerCity(buyer.getLocality()),
            new BuyerRegion(buyer.getRegion()),
            new BuyerPostalCode(buyer.getPostalCode()),
            new BuyerCountry(buyer.getCountry()),
            new BuyerEmailAddress(buyer.getEmail()),
            new BuyerPhoneNumber(buyer.getPhone()),
            new BuyerNotify(buyer.getNotify()),
            new BuyerProvidedEmail(bitPayInvoice.getBuyerProvidedEmail())
        );
    }
}
