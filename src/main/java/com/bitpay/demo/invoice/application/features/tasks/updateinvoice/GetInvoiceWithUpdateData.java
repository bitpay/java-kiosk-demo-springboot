/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.updateinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.BitPayOrderId;
import com.bitpay.demo.invoice.domain.BitPayUrl;
import com.bitpay.demo.invoice.domain.CurrencyCode;
import com.bitpay.demo.invoice.domain.ExceptionStatus;
import com.bitpay.demo.invoice.domain.Invoice;
import com.bitpay.demo.invoice.domain.Price;
import com.bitpay.demo.invoice.domain.Status;
import com.bitpay.demo.invoice.domain.buyer.BuyerAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerCity;
import com.bitpay.demo.invoice.domain.buyer.BuyerCountry;
import com.bitpay.demo.invoice.domain.buyer.BuyerEmailAddress;
import com.bitpay.demo.invoice.domain.buyer.BuyerName;
import com.bitpay.demo.invoice.domain.buyer.BuyerNotify;
import com.bitpay.demo.invoice.domain.buyer.BuyerPhoneNumber;
import com.bitpay.demo.invoice.domain.buyer.BuyerPostalCode;
import com.bitpay.demo.invoice.domain.buyer.BuyerRegion;
import com.bitpay.demo.invoice.domain.buyer.InvoiceBuyer;
import com.bitpay.demo.invoice.domain.payment.AmountPaid;
import com.bitpay.demo.invoice.domain.payment.InvoicePayment;
import com.bitpay.demo.invoice.domain.payment.TransactionCurrency;
import com.bitpay.demo.shared.StringToObjectParser;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.NonNull;

@DependencyInjection
class GetInvoiceWithUpdateData {

    private final StringToObjectParser stringToObjectParser;

    GetInvoiceWithUpdateData(@NonNull final StringToObjectParser stringToObjectParser) {
        this.stringToObjectParser = stringToObjectParser;
    }

    @NonNull
    public Invoice execute(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Invoice invoice
    ) throws ReflectiveOperationException {
        return new Invoice(
            invoice.getUuid(),
            invoice.getPosData(),
            (Price) getFieldValue("price", updateData, Price.class, invoice.getPrice()),
            (CurrencyCode) getFieldValue("currency", updateData, CurrencyCode.class, invoice.getCurrencyCode()),
            invoice.getBitPayId(),
            (Status) getFieldValue("status", updateData, Status.class, invoice.getStatus()),
            invoice.getCreatedDate(),
            invoice.getTransactionSpeed(),
            getPayment(updateData, invoice.getInvoicePayment()),
            getInvoiceBuyer(invoice.getInvoiceBuyer(), (Map<String, Object>) updateData.get("buyerFields")),
            invoice.getInvoiceRefund(),
            (BitPayOrderId) getFieldValue("orderId", updateData, BitPayOrderId.class, invoice.getBitPayOrderId()),
            getExpirationTime(updateData, invoice),
            invoice.getFacadeType(),
            invoice.getBitPayGuid(),
            (ExceptionStatus) getFieldValue(
                "exceptionStatus",
                updateData,
                ExceptionStatus.class,
                invoice.getExceptionStatus()
            ),
            (BitPayUrl) getFieldValue("url", updateData, BitPayUrl.class, invoice.getBitPayUrl()),
            invoice.getRedirectUrl(),
            invoice.getCloseUrl(),
            invoice.getAcceptanceWindow(),
            invoice.getToken(),
            invoice.getMerchantName(),
            invoice.getItemDescription(),
            invoice.getBillId(),
            invoice.getTargetConfirmations(),
            invoice.getLowFeeDetected(),
            invoice.getAutoRedirect(),
            invoice.getShopperUser(),
            invoice.getJsonPayProRequired(),
            invoice.getBitPayIdRequired(),
            invoice.getIsCancelled()
        );
    }

    @NonNull
    private InvoicePayment getPayment(
        @NonNull final Map<String, Object> updateData,
        @NonNull final InvoicePayment invoicePayment
    ) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        return new InvoicePayment(
            (AmountPaid) getFieldValue(
                "amountPaid",
                updateData,
                AmountPaid.class,
                invoicePayment.getAmountPaid()
            ),
            invoicePayment.getDisplayAmountPaid(),
            invoicePayment.getNonPayProPaymentReceived(),
            invoicePayment.getUniversalCodesPaymentString(),
            invoicePayment.getUniversalCodesVerificationLink(),
            (TransactionCurrency) getFieldValue(
                "transactionCurrency",
                updateData,
                TransactionCurrency.class,
                invoicePayment.getTransactionCurrency()
            ),
            invoicePayment.getUnderpaidAmount(),
            invoicePayment.getOverpaidAmount()
        );
    }

    @Nullable
    private LocalDateTime getExpirationTime(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Invoice invoice
    ) {
        if (!updateData.containsKey("expirationTime")) {
            return invoice.getExpirationTime();
        }

        final var expirationTime = updateData.get("expirationTime");
        if (Objects.isNull(expirationTime)) {
            return null;
        }

        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli((Long) expirationTime),
            ZoneId.systemDefault()
        );
    }

    @NonNull
    private InvoiceBuyer getInvoiceBuyer(
        @NonNull final InvoiceBuyer invoiceBuyer,
        @Nullable final Map<String, Object> updateData
    ) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (Objects.isNull(updateData)) {
            return invoiceBuyer;
        }

        return new InvoiceBuyer(
            invoiceBuyer.getInvoiceBuyerProvidedInfo(),
            (BuyerName) getFieldValue("buyerName", updateData, BuyerName.class, invoiceBuyer.getName()),
            (BuyerAddress) getFieldValue("buyerAddress1", updateData, BuyerAddress.class, invoiceBuyer.getAddress1()),
            (BuyerAddress) getFieldValue("buyerAddress2", updateData, BuyerAddress.class, invoiceBuyer.getAddress2()),
            (BuyerCity) getFieldValue("buyerCity", updateData, BuyerCity.class, invoiceBuyer.getCity()),
            (BuyerRegion) getFieldValue("buyerState", updateData, BuyerRegion.class, invoiceBuyer.getRegion()),
            (BuyerPostalCode) getFieldValue(
                "buyerZip",
                updateData,
                BuyerPostalCode.class,
                invoiceBuyer.getPostalCode()
            ),
            (BuyerCountry) getFieldValue("buyerCountry", updateData, BuyerCountry.class, invoiceBuyer.getCountry()),
            (BuyerEmailAddress) getFieldValue(
                "buyerEmail",
                updateData,
                BuyerEmailAddress.class,
                invoiceBuyer.getEmail()
            ),
            (BuyerPhoneNumber) getFieldValue("buyerPhone", updateData, BuyerPhoneNumber.class, invoiceBuyer.getPhone()),
            (BuyerNotify) getFieldValue("buyerNotify", updateData, BuyerNotify.class, invoiceBuyer.getNotify()),
            invoiceBuyer.getBuyerProvidedEmail()
        );
    }

    @Nullable
    private Object getFieldValue(
        @NonNull final String fieldName,
        @NonNull final Map<String, Object> updateData,
        @NonNull final Class<?> valueObjectClass,
        @Nullable final Object defaultValue
    ) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (!updateData.containsKey(fieldName)) {
            return defaultValue;
        }

        final var value = updateData.get(fieldName);
        if (Objects.isNull(value)) {
            return null;
        }

        final var parameterType = valueObjectClass.getDeclaredConstructors()[0].getParameterTypes()[0];

        return valueObjectClass.getDeclaredConstructors()[0].newInstance(
            this.stringToObjectParser.parse(
                parameterType,
                value.toString()
            )
        );
    }
}
