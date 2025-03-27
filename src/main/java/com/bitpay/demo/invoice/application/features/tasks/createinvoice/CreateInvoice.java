/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.InvoiceRepository;
import com.bitpay.demo.invoice.domain.InvoiceUuid;
import com.bitpay.demo.shared.form.GetValidatedParams;
import com.bitpay.demo.shared.form.MissingRequiredField;
import com.bitpay.demo.shared.logger.LogCode;
import com.bitpay.demo.shared.logger.Logger;
import com.bitpay.sdk.exceptions.BitPayException;
import com.bitpay.sdk.model.invoice.Invoice;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

@DependencyInjection
public class CreateInvoice {

    private final GetValidatedParams getValidatedParams;
    private final CreateBitPayInvoice createBitPayInvoice;
    private final InvoiceFactory invoiceFactory;
    private final InvoiceRepository invoiceRepository;
    private final Logger logger;

    CreateInvoice(
        @NonNull final GetValidatedParams getValidatedParams,
        @NonNull final CreateBitPayInvoice createBitPayInvoice,
        @NonNull final InvoiceFactory invoiceFactory,
        @NonNull final InvoiceRepository invoiceRepository,
        @NonNull final Logger logger
    ) {
        this.getValidatedParams = getValidatedParams;
        this.createBitPayInvoice = createBitPayInvoice;
        this.invoiceFactory = invoiceFactory;
        this.invoiceRepository = invoiceRepository;
        this.logger = logger;
    }

    @NonNull
    public String execute(@NonNull final Map<String, Object> requestParameters)
        throws MissingRequiredField, BitPayException {

        final Map<String, Object> validatedParams = this.getValidatedParams.execute(requestParameters);
        final InvoiceUuid uuid = new InvoiceUuid(UUID.randomUUID().toString());
        final Invoice bitPayInvoice = this.createBitPayInvoice.execute(validatedParams, uuid);
        final com.bitpay.demo.invoice.domain.Invoice invoice = this.invoiceFactory.create(bitPayInvoice, uuid);

        this.invoiceRepository.save(invoice);

        this.logger.info(
            LogCode.INVOICE_CREATE_SUCCESS,
            "Successfully created invoice",
            Map.of("id", invoice.getId())
        );

        return bitPayInvoice.getUrl();
    }
}
