/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.InvoiceUuid;
import com.bitpay.demo.shared.ObjectToJsonConverter;
import com.bitpay.demo.shared.bitpayproperties.BitPayProperties;
import com.bitpay.sdk.Client;
import com.bitpay.sdk.exceptions.BitPayException;
import com.bitpay.sdk.model.invoice.Buyer;
import com.bitpay.sdk.model.invoice.Invoice;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@DependencyInjection
class CreateBitPayInvoice {

    private final Client bitpayClient;
    private final BitPayProperties bitPayProperties;
    private final ObjectToJsonConverter objectToJsonConverter;
    private final GetNotificationUrl getNotificationUrl;

    CreateBitPayInvoice(
        @NonNull final Client bitpayClient,
        @NonNull final BitPayProperties bitPayProperties,
        @NonNull final ObjectToJsonConverter objectToJsonConverter,
        @NonNull final GetNotificationUrl getNotificationUrl
    ) {
        this.bitpayClient = bitpayClient;
        this.bitPayProperties = bitPayProperties;
        this.objectToJsonConverter = objectToJsonConverter;
        this.getNotificationUrl = getNotificationUrl;
    }

    @NonNull
    public Invoice execute(
        @NonNull final Map<String, Object> validatedParams,
        @NonNull final InvoiceUuid uuid
    ) throws BitPayException {
        final Double price = Double.parseDouble(validatedParams.get("price").toString());
        final String posData = this.objectToJsonConverter.execute(validatedParams);
        final Invoice invoice = new Invoice(price, this.bitPayProperties.getCurrency());
        invoice.setOrderId(UUID.randomUUID().toString());
        invoice.setNotificationEmail(this.bitPayProperties.getNotificationEmail());
        invoice.setTransactionSpeed("medium");
        invoice.setItemDesc(StringUtils.capitalize(this.bitPayProperties.getMode()));
        invoice.setPosData(posData);
        invoice.setNotificationUrl(this.getNotificationUrl.execute(uuid));
        invoice.setExtendedNotifications(true);

        if (validatedParams.keySet().stream().anyMatch(key -> key.startsWith("buyer"))) {
            final Buyer buyer = getBuyer(validatedParams);

            invoice.setBuyer(buyer);
        }

        return this.bitpayClient.createInvoice(invoice);
    }

    @NonNull
    private static Buyer getBuyer(@NonNull final Map<String, Object> validatedParams) {
        final var buyer = new Buyer();
        buyer.setName((String) validatedParams.getOrDefault("buyerName", null));
        buyer.setAddress1((String) validatedParams.getOrDefault("buyerAddress1", null));
        buyer.setAddress2((String) validatedParams.getOrDefault("buyerAddress2", null));
        buyer.setLocality((String) validatedParams.getOrDefault("buyerLocality", null));
        buyer.setRegion((String) validatedParams.getOrDefault("buyerRegion", null));
        buyer.setPostalCode((String) validatedParams.getOrDefault("buyerPostalCode", null));
        buyer.setCountry("US");
        buyer.setEmail((String) validatedParams.getOrDefault("buyerEmail", null));
        buyer.setPhone((String) validatedParams.getOrDefault("buyerPhone", null));

        return buyer;
    }
}
