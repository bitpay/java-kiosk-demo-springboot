/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.infrastructure.ui.createinvoice;

import com.bitpay.demo.AbstractUiIntegrationTest;
import com.bitpay.sdk.Client;
import com.bitpay.sdk.exceptions.InvoiceCreationException;
import com.bitpay.sdk.model.invoice.Invoice;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreateInvoiceIntegrationTest extends AbstractUiIntegrationTest {

    private static final String URL = "/invoice";

    @MockBean
    private Client bitpayClient;

    @BeforeEach
    public void initialize() {
        super.initialize();
    }

    @Test
    public void createInvoiceForSpecifiedParams() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any())).thenReturn(getBitPayInvoice());

        // when
        final var result = getResultActions(
            Map.of(
                "store", "store-1",
                "register", "2",
                "reg_transaction_no", "test",
                "price", 123
            )
        );

        result.andExpect(MockMvcResultMatchers.status().isMovedPermanently());
        Assertions.assertEquals(1, this.invoiceRepository.findAll().size());
    }

    @Test
    public void doNotCreateInvoiceAndBackToInvoiceFormPageWhenBitPayReturnsError() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any()))
            .thenThrow(new InvoiceCreationException("123", "test"));

        // when
        final var result = getResultActions(
            Map.of(
                "store", "store-1",
                "register", "2",
                "reg_transaction_no", "test",
                "price", 123
            )
        );

        result.andExpect(MockMvcResultMatchers.redirectedUrl(""));
        Assertions.assertEquals(0, this.invoiceRepository.findAll().size());
    }

    @Test
    public void doNotCreateInvoiceAndBackToInvoiceFormPageWhenMissingRequiredValue() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any()))
            .thenThrow(new InvoiceCreationException("123", "test"));

        // when
        final var result = getResultActions(
            Map.of(
                "store", "store-1",
                "register", "2",
                "reg_transaction_no", "test"
            )
        );

        // then
        result.andExpect(MockMvcResultMatchers.redirectedUrl(""));
        Assertions.assertEquals(0, this.invoiceRepository.findAll().size());
    }

    private ResultActions getResultActions(final Map<String, Object> request) throws Exception {
        return post(URL, request);
    }

    private Invoice getBitPayInvoice() {
        final String bitPayInvoiceJson = getDataFromFile("bitPayInvoice.json");
        return toObject(bitPayInvoiceJson, Invoice.class);
    }
}
