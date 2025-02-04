/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.infrastructure.features.tasks.updateinvoice;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.bitpay.demo.DependencyInjection;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.NonNull;

@DependencyInjection
public class WebhookVerifier {
    public Boolean verify(
        @NonNull final String signingKey,
        @NonNull final String sigHeader,
        @NonNull final String webhookBody
    ) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String algorithm = "HmacSHA256";

        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(UTF_8), algorithm);
        mac.init(secretKeySpec);

        byte[] signatureBytes = mac.doFinal(webhookBody.getBytes(UTF_8));

        String calculated = Base64.getEncoder().encodeToString(signatureBytes);
        Boolean match = sigHeader.equals(calculated);

        return match;
    }
}
