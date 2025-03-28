/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.donation.infrastructure.ui.createdonation;

import com.bitpay.demo.AbstractUiIntegrationTest;
import com.bitpay.sdk.Client;
import com.bitpay.sdk.exceptions.InvoiceCreationException;
import com.bitpay.sdk.model.invoice.Invoice;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@TestPropertySource(properties = {
    "spring.config.location=classpath:application-integrationtest.yaml",
    "bitpay.design.mode=donation"
})
public class CreateDonationIntegrationTest extends AbstractUiIntegrationTest {

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
        final var params = new HashMap<String, Object>();
        params.put("buyerName", "fake name");
        params.put("buyerAddress1", "fake address");
        params.put("buyerAddress2", "fake address 2");
        params.put("buyerLocality", "fake location");
        params.put("buyerRegion", "AK");
        params.put("buyerPostalCode", "10010");
        params.put("buyerPhone", "123456789");
        params.put("buyerEmail", "test@example.com");
        params.put("store", "store-1");
        params.put("register", "2");
        params.put("reg_transaction_no", "test");
        params.put("price", "100");
        params.put("fake", "123");

        // when
        final var result = getResultActions(params);

        result.andExpect(MockMvcResultMatchers.status().isMovedPermanently());
        Assertions.assertEquals(1, this.invoiceRepository.findAll().size());
    }

    @Test
    public void doNotCreateInvoiceAndBackToInvoiceFormPageWhenBitPayReturnsError() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any()))
            .thenThrow(new InvoiceCreationException("123", "test"));
        final var params = new HashMap<String, Object>();
        params.put("buyerName", "fake name");
        params.put("buyerAddress1", "fake address");
        params.put("buyerAddress2", "fake address 2");
        params.put("buyerLocality", "fake location");
        params.put("buyerRegion", "AK");
        params.put("buyerPostalCode", "10010");
        params.put("buyerPhone", "123456789");
        params.put("buyerEmail", "test@example.com");
        params.put("store", "store-1");
        params.put("register", "2");
        params.put("reg_transaction_no", "test");
        params.put("price", "100");
        params.put("fake", "123");

        // when
        final var result = getResultActions(params);

        result.andExpect(MockMvcResultMatchers.redirectedUrl(""));
        Assertions.assertEquals(0, this.invoiceRepository.findAll().size());
    }

    @Test
    public void doNotCreateInvoiceAndBackToInvoiceFormPageWhenMissingRequiredValue() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any()))
            .thenThrow(new InvoiceCreationException("123", "test"));
        final var params = new HashMap<String, Object>();
        params.put("buyerName", "fake name");
        params.put("buyerAddress1", "fake address");
        params.put("buyerAddress2", "fake address 2");
        params.put("buyerLocality", "fake location");
        params.put("buyerRegion", "AK");
        params.put("buyerPostalCode", "10010");
        params.put("buyerPhone", "123456789");
        params.put("buyerEmail", "test@example.com");
        params.put("store", "store-1");
        params.put("register", "2");
        params.put("reg_transaction_no", "test");
        params.put("fake", "123");

        // when
        final var result = getResultActions(params);

        // then
        result.andExpect(MockMvcResultMatchers.redirectedUrl(""));
        Assertions.assertEquals(0, this.invoiceRepository.findAll().size());
    }

    @Test
    public void doNotCreateInvoiceAndBackToInvoiceFormPageWhenMissingRequiredPosValue() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any()))
            .thenThrow(new InvoiceCreationException("123", "test"));
        final var params = new HashMap<String, Object>();
        params.put("buyerName", "fake name");
        params.put("buyerAddress1", "fake address");
        params.put("buyerAddress2", "fake address 2");
        params.put("buyerLocality", "fake location");
        params.put("buyerRegion", "AK");
        params.put("buyerPostalCode", "10010");
        params.put("buyerPhone", "123456789");
        params.put("buyerEmail", "test@example.com");
        params.put("store", "store-1");
        params.put("register", "2");
        params.put("price", "100");
        params.put("fake", "123");

        // when
        final var result = getResultActions(params);

        // then
        result.andExpect(MockMvcResultMatchers.redirectedUrl(""));
        Assertions.assertEquals(0, this.invoiceRepository.findAll().size());
    }

    @Test
    public void doNotCreateInvoiceAndBackToInvoiceFormPageWhenParamHasWrongValue() throws Exception {
        // given
        Mockito.when(this.bitpayClient.createInvoice(ArgumentMatchers.any()))
            .thenThrow(new InvoiceCreationException("123", "test"));
        final var params = new HashMap<String, Object>();
        params.put("buyerName", "fake name");
        params.put("buyerAddress1", "fake address");
        params.put("buyerAddress2", "fake address 2");
        params.put("buyerLocality", "fake location");
        params.put("buyerRegion", "123");
        params.put("buyerPostalCode", "10010");
        params.put("buyerPhone", "123456789");
        params.put("buyerEmail", "test@example.com");
        params.put("store", "store-1");
        params.put("register", "2");
        params.put("reg_transaction_no", "test");
        params.put("price", "100");
        params.put("fake", "123");

        // when
        final var result = getResultActions(params);

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
