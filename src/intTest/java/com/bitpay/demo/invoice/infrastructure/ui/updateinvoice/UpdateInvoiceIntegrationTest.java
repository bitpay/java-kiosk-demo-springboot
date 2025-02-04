/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.infrastructure.ui.updateinvoice;

import com.bitpay.demo.AbstractUiIntegrationTest;
import com.bitpay.demo.invoice.domain.Invoice;
import java.util.Map;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class UpdateInvoiceIntegrationTest extends AbstractUiIntegrationTest {

    private static final String URL = "/invoices/";

    @BeforeEach
    public void initialize() {
        super.initialize();
    }

    @Test
    public void shouldUpdateInvoice() throws Exception {
        // given
        final var invoice = createInvoice();

        // when
        final var result = getResultActions(
            invoice.getUuid().value(),
            getDataFromFile("updateData.json"),
            Map.of("x-signature", "dIFy1UloHI6k5rpdVdl3cADtPF01g/xHKt6rqKuo5Ls=")
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertEquals(
            "expired",
            this.invoiceRepository.findById(invoice.getInvoiceId()).getStatus().value()
        );
    }

    @Test
    public void shouldNotUpdateInvoiceWhenInvoiceForUuidDoesNotExists() throws Exception {
        // given
        final var invoice = createInvoice();

        // when
        final var result = getResultActions(
            "12312412",
            getDataFromFile("updateData.json"),
            Map.of("x-signature", "dIFy1UloHI6k5rpdVdl3cADtPF01g/xHKt6rqKuo5Ls=")
        );

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        Assertions.assertEquals(
            "new",
            this.invoiceRepository.findById(invoice.getInvoiceId()).getStatus().value()
        );
    }

    @Test
    public void shouldNotUpdateInvoiceWhenUpdateDataAreInvalid() throws Exception {
        // given
        final var invoice = createInvoice();

        // when
        final var result = getResultActions(
            invoice.getUuid().value(),
            getDataFromFile("invalidUpdateData.json"),
            Map.of("x-signature", "33SW42rFbxKuGj7s4166nOrWuHHHM1EMxgHmgT5tksU=")
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().json(getDataFromFile("invalidUpdateDataResponse.json"), false));
        Assertions.assertEquals(
            "new",
            this.invoiceRepository.findById(invoice.getInvoiceId()).getStatus().value()
        );
    }

    @Test
    public void shouldNotUpdateInvoiceWhenWebhookSignatureVerificationFailed() throws Exception {
        // given
        final var invoice = createInvoice();

        // when
        final var result = getResultActions(
            "12312412",
            getDataFromFile("updateData.json"),
            Map.of("x-signature", "randomsignature")
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertEquals(
            "new",
            this.invoiceRepository.findById(invoice.getInvoiceId()).getStatus().value()
        );
    }

    private ResultActions getResultActions(
        final String invoiceUuId,
        final String requestBody,
        @Nullable final Map<String, String> headers
    ) throws Exception {
        return post(URL + invoiceUuId, requestBody, headers);
    }

    private Invoice createInvoice() {
        final String invoiceJson = getDataFromFile("invoice.json");
        final var invoice = toObject(invoiceJson, Invoice.class);
        this.invoiceRepository.save(invoice);

        return invoice;
    }
}
