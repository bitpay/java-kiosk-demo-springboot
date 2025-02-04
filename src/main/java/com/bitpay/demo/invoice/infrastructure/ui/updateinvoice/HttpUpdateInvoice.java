/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.infrastructure.ui.updateinvoice;

import com.bitpay.demo.invoice.application.features.tasks.updateinvoice.UpdateInvoice;
import com.bitpay.demo.invoice.application.features.tasks.updateinvoice.ValidationInvoiceUpdateDataFailed;
import com.bitpay.demo.invoice.domain.InvoiceNotFound;
import com.bitpay.demo.invoice.domain.InvoiceUuid;
import com.bitpay.demo.invoice.infrastructure.features.tasks.updateinvoice.WebhookVerifier;
import com.bitpay.demo.shared.bitpayproperties.BitPayProperties;
import com.bitpay.demo.shared.logger.LogCode;
import com.bitpay.demo.shared.logger.Logger;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpUpdateInvoice {
    private final UpdateInvoice updateInvoice;
    private final BitPayProperties bitPayProperties;
    private final WebhookVerifier webhookVerifier;
    private final Logger logger;

    public HttpUpdateInvoice(
        @NonNull final UpdateInvoice updateInvoice,
        @NonNull final BitPayProperties bitPayProperties,
        @NonNull final WebhookVerifier webhookVerifier,
        @NonNull final Logger logger
    ) {
        this.updateInvoice = updateInvoice;
        this.webhookVerifier = webhookVerifier;
        this.bitPayProperties = bitPayProperties;
        this.logger = logger;
    }

    @PostMapping("/invoices/{uuid}")
    public void execute(
        @NonNull @PathVariable("uuid") final String invoiceUuid,
        @NonNull @RequestBody final String requestBody,
        @NonNull @RequestHeader("x-signature") final String signature
    ) throws ReflectiveOperationException,
        ValidationInvoiceUpdateDataFailed,
        InvoiceNotFound,
        NoSuchAlgorithmException,
        InvalidKeyException,
        UnsupportedEncodingException {
        if (this.webhookVerifier.verify(this.bitPayProperties.getToken(), signature, requestBody)) {
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Gson gson = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();
            Map<String, Object> updateData = gson.fromJson(requestBody, mapType);

            Map<String, Object> data = (Map<String, Object>) updateData.get("data");
            Map<String, Object> event = (Map<String, Object>) updateData.get("event");
            if (!event.isEmpty() && event.containsKey("name")) {
                data.put("eventName", event.get("name"));
            }

            this.updateInvoice.execute(
                new InvoiceUuid(invoiceUuid),
                data
            );
        } else {
            this.logger.error(
                LogCode.IPN_SIGNATURE_VERIFICATION_FAIL,
                "Webhook signature verification failed",
                Map.of(
                    "uuid", invoiceUuid
                )
            );
        }
    }
}
