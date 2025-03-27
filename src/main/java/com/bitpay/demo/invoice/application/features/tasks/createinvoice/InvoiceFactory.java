/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.AcceptanceWindow;
import com.bitpay.demo.invoice.domain.AutoRedirect;
import com.bitpay.demo.invoice.domain.BillId;
import com.bitpay.demo.invoice.domain.BitPayGuid;
import com.bitpay.demo.invoice.domain.BitPayId;
import com.bitpay.demo.invoice.domain.BitPayIdRequired;
import com.bitpay.demo.invoice.domain.BitPayOrderId;
import com.bitpay.demo.invoice.domain.BitPayUrl;
import com.bitpay.demo.invoice.domain.CloseUrl;
import com.bitpay.demo.invoice.domain.CurrencyCode;
import com.bitpay.demo.invoice.domain.ExceptionStatus;
import com.bitpay.demo.invoice.domain.FacadeType;
import com.bitpay.demo.invoice.domain.Invoice;
import com.bitpay.demo.invoice.domain.InvoiceUuid;
import com.bitpay.demo.invoice.domain.IsCancelled;
import com.bitpay.demo.invoice.domain.ItemDescription;
import com.bitpay.demo.invoice.domain.JsonPayProRequired;
import com.bitpay.demo.invoice.domain.LowFeeDetected;
import com.bitpay.demo.invoice.domain.MerchantName;
import com.bitpay.demo.invoice.domain.PosDataJson;
import com.bitpay.demo.invoice.domain.Price;
import com.bitpay.demo.invoice.domain.RedirectUrl;
import com.bitpay.demo.invoice.domain.ShopperUser;
import com.bitpay.demo.invoice.domain.Status;
import com.bitpay.demo.invoice.domain.TargetConfirmations;
import com.bitpay.demo.invoice.domain.Token;
import com.bitpay.demo.invoice.domain.TransactionSpeed;
import com.bitpay.demo.invoice.domain.itemizeddetail.InvoiceItemizedDetail;
import com.bitpay.sdk.model.invoice.InvoiceItemizedDetails;
import com.bitpay.sdk.model.invoice.InvoiceTransaction;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.NonNull;

@DependencyInjection
class InvoiceFactory {

    private final InvoicePaymentFactory invoicePaymentFactory;
    private final InvoiceBuyerFactory invoiceBuyerFactory;
    private final InvoiceRefundFactory invoiceRefundFactory;
    private final InvoiceTransactionFactory invoiceTransactionFactory;
    private final InvoiceItemizedDetailFactory invoiceItemizedDetailFactory;

    InvoiceFactory(
        @NonNull final InvoicePaymentFactory invoicePaymentFactory,
        @NonNull final InvoiceBuyerFactory invoiceBuyerFactory,
        @NonNull final InvoiceRefundFactory invoiceRefundFactory,
        @NonNull final InvoiceTransactionFactory invoiceTransactionFactory,
        @NonNull final InvoiceItemizedDetailFactory invoiceItemizedDetailFactory
    ) {
        this.invoicePaymentFactory = invoicePaymentFactory;
        this.invoiceBuyerFactory = invoiceBuyerFactory;
        this.invoiceRefundFactory = invoiceRefundFactory;
        this.invoiceTransactionFactory = invoiceTransactionFactory;
        this.invoiceItemizedDetailFactory = invoiceItemizedDetailFactory;
    }

    @NonNull
    Invoice create(
        @NonNull final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice,
        @NonNull final InvoiceUuid uuid
    ) {
        final var invoice = new Invoice(
            uuid,
            new PosDataJson(bitPayInvoice.getPosData()),
            new Price(bitPayInvoice.getPrice()),
            new CurrencyCode(bitPayInvoice.getCurrency()),
            new BitPayId(bitPayInvoice.getId()),
            new Status(bitPayInvoice.getStatus()),
            LocalDateTime.ofInstant(Instant.ofEpochMilli(bitPayInvoice.getInvoiceTime()), ZoneId.systemDefault()),
            new TransactionSpeed(bitPayInvoice.getTransactionSpeed()),
            this.invoicePaymentFactory.create(bitPayInvoice),
            this.invoiceBuyerFactory.create(bitPayInvoice, bitPayInvoice.getInvoiceBuyerProvidedInfo()),
            this.invoiceRefundFactory.create(bitPayInvoice),
            new BitPayOrderId(bitPayInvoice.getOrderId()),
            LocalDateTime.ofInstant(Instant.ofEpochMilli(bitPayInvoice.getExpirationTime()), ZoneId.systemDefault()),
            new FacadeType("pos/invoice"),
            new BitPayGuid(bitPayInvoice.getGuid()),
            new ExceptionStatus(bitPayInvoice.getExceptionStatus()),
            new BitPayUrl(bitPayInvoice.getUrl()),
            new RedirectUrl(bitPayInvoice.getRedirectUrl()),
            new CloseUrl(bitPayInvoice.getCloseUrl()),
            new AcceptanceWindow(bitPayInvoice.getAcceptanceWindow()),
            new Token(bitPayInvoice.getToken()),
            new MerchantName(bitPayInvoice.getMerchantName()),
            new ItemDescription(bitPayInvoice.getItemDesc()),
            new BillId(bitPayInvoice.getBillId()),
            new TargetConfirmations(bitPayInvoice.getTargetConfirmations()),
            new LowFeeDetected(bitPayInvoice.getLowFeeDetected()),
            new AutoRedirect(bitPayInvoice.getAutoRedirect()),
            new ShopperUser(bitPayInvoice.getShopper().getName()),
            new JsonPayProRequired(bitPayInvoice.getJsonPayProRequired()),
            new BitPayIdRequired(bitPayInvoice.getBitpayIdRequired()),
            new IsCancelled(bitPayInvoice.getIsCancelled())
        );

        invoice.addInvoiceTransactions(
            getInvoiceTransactions(
                invoice,
                bitPayInvoice.getTransactions()
            )
        );
        invoice.addInvoiceItemizedDetails(
            getInvoiceItemizedDetails(
                invoice,
                bitPayInvoice.getItemizedDetails()
            )
        );

        return invoice;
    }

    @NonNull
    private Collection<InvoiceItemizedDetail> getInvoiceItemizedDetails(
        @NonNull final Invoice invoice,
        @Nullable final Collection<InvoiceItemizedDetails> itemizedDetails
    ) {
        if (Objects.isNull(itemizedDetails)) {
            return List.of();
        }

        return itemizedDetails.stream()
            .map(itemizedDetail -> this.invoiceItemizedDetailFactory.create(invoice, itemizedDetail))
            .collect(Collectors.toList());
    }

    @NonNull
    private Collection<com.bitpay.demo.invoice.domain.transaction.InvoiceTransaction> getInvoiceTransactions(
        @NonNull final Invoice invoice,
        @Nullable final Collection<InvoiceTransaction> transactions
    ) {
        if (Objects.isNull(transactions)) {
            return List.of();
        }

        return transactions.stream()
            .map(transaction -> this.invoiceTransactionFactory.create(invoice, transaction))
            .collect(Collectors.toList());
    }
}
