/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.shared.infrastructure;

import com.bitpay.demo.shared.bitpayproperties.BitPayProperties;
import com.bitpay.sdk.Client;
import com.bitpay.sdk.Environment;
import com.bitpay.sdk.PosToken;
import com.bitpay.sdk.exceptions.BitPayException;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BitPayClientConfiguration {
    @Bean
    public @NonNull Client bitpayClient(@NonNull final BitPayProperties bitPayProperties) throws BitPayException {
        final Client client = new Client(new PosToken(bitPayProperties.getToken()), Environment.TEST);

        return client;
    }
}
