/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.UnitTest;
import com.bitpay.sdk.model.invoice.Invoice;

interface GetBitPayInvoice extends UnitTest {

    default Invoice getBitPayInvoice() {
        final String bitPayInvoiceJson = getDataFromFile("bitPayInvoice.json");
        return toObject(bitPayInvoiceJson, Invoice.class);
    }
}
