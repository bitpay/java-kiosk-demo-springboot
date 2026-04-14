/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.infrastructure.ui.createinvoice;

import com.bitpay.demo.invoice.application.features.tasks.createinvoice.CreateInvoice;
import com.bitpay.sdk.exceptions.BitPayException;
import java.net.URI;
import java.util.Map;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpCreateInvoice {
    private final CreateInvoice createInvoice;

    public HttpCreateInvoice(@NonNull final CreateInvoice createInvoice) {
        this.createInvoice = createInvoice;
    }

    @PostMapping("/invoice")
    public ResponseEntity<Void> execute(@NonNull @RequestParam final Map<String, Object> params) throws BitPayException {
        final String url = this.createInvoice.execute(params);

        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url + "&context=mt"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
