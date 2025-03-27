/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.updateinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.BitPayId;
import com.bitpay.demo.shared.StringToObjectParser;
import com.bitpay.sdk.model.invoice.InvoiceStatus;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import org.thymeleaf.util.StringUtils;

@DependencyInjection
class ValidateUpdateData {

    private final StringToObjectParser stringToObjectParser;

    private final Collection<String> allowedStatuses = List.of(
        InvoiceStatus.New,
        InvoiceStatus.Paid,
        InvoiceStatus.Confirmed,
        InvoiceStatus.Complete,
        InvoiceStatus.Expired,
        InvoiceStatus.Invalid,
        InvoiceStatus.Declined
    );

    private final Collection<String> allowedExceptionStatus = List.of(
        "false",
        "paidPartial",
        "paidOver"
    );

    ValidateUpdateData(@NonNull final StringToObjectParser stringToObjectParser) {
        this.stringToObjectParser = stringToObjectParser;
    }

    void execute(
        @NonNull final Map<String, Object> updateData,
        final @NonNull BitPayId bitPayId
    ) throws ValidationInvoiceUpdateDataFailed {
        final Map<String, String> errors = new HashMap<>();

        validateId(updateData, bitPayId, errors);
        validatePrice(updateData, errors);
        validateString(updateData, "currency", 10, errors);
        validateStatus(updateData, errors);
        validateString(updateData, "orderId", 255, errors);
        validateExceptionStatus(updateData, errors);
        validateString(updateData, "url", 255, errors);
        validateAmountPaid(updateData, errors);
        validateString(updateData, "transactionCurrency", 10, errors);
        validateExpirationTime(updateData, errors);
        validateBuyerFields(updateData, errors);

        if (!errors.isEmpty()) {
            throw new ValidationInvoiceUpdateDataFailed(errors);
        }
    }

    private void validateBuyerFields(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("buyerFields")) {
            return;
        }


        if (!(updateData.get("buyerFields") instanceof Map<?, ?>)) {
            errors.put("buyerFields", "BuyerFields isn't object.");
            return;
        }

        final Map<String, Object> buyerFields = (Map<String, Object>) updateData.get("buyerFields");

        validateString(buyerFields, "buyerName", 255, errors);
        validateString(buyerFields, "buyerAddress1", 255, errors);
        validateString(buyerFields, "buyerAddress2", 255, errors);
        validateString(buyerFields, "buyerCity", 255, errors);
        validateString(buyerFields, "buyerState", 255, errors);
        validateString(buyerFields, "buyerZip", 255, errors);
        validateString(buyerFields, "buyerCountry", 2, errors);
        validateString(buyerFields, "buyerPhone", 255, errors);
        validateString(buyerFields, "buyerEmail", 255, errors);

        if (!buyerFields.containsKey("buyerNotify")) {
            return;
        }

        try {
            this.stringToObjectParser.parse(Boolean.class, buyerFields.get("buyerNotify").toString());
        } catch (final UnsupportedOperationException ignore) {
            errors.put("buyerNotify", "BuyerNotify is not boolean.");
        }
    }

    private void validateExpirationTime(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("expirationTime")) {
            return;
        }

        try {
            this.stringToObjectParser.parse(Long.class, updateData.get("expirationTime").toString());
        } catch (final UnsupportedOperationException | NumberFormatException ignore) {
            errors.put("expirationTime", "ExpirationTime is not number.");
        }
    }

    private void validateAmountPaid(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("amountPaid")) {
            return;
        }

        try {
            this.stringToObjectParser.parse(Double.class, updateData.get("amountPaid").toString());
        } catch (final UnsupportedOperationException | NumberFormatException ignore) {
            errors.put("amountPaid", "AmountPaid is not number.");
        }
    }

    private void validateExceptionStatus(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("exceptionStatus")) {
            return;
        }

        if (!this.allowedExceptionStatus.contains(updateData.get("exceptionStatus").toString())) {
            errors.put("exceptionStatus", "ExceptionStatus has wrong type.");
        }
    }

    private void validateStatus(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("status")) {
            errors.put("status", "Status is empty.");
            return;
        }

        if (!this.allowedStatuses.contains(updateData.get("status").toString())) {
            errors.put("status", "Status has wrong type.");
        }
    }

    private void validatePrice(
        @NonNull final Map<String, Object> updateData,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("price")) {
            return;
        }

        try {
            this.stringToObjectParser.parse(Double.class, updateData.get("price").toString());
        } catch (final UnsupportedOperationException | NumberFormatException ignore) {
            errors.put("price", "Price is not number.");
        }
    }

    private static void validateId(
        @NonNull final Map<String, Object> updateData,
        @NonNull final BitPayId bitPayId,
        @NonNull final Map<String, String> errors
    ) {
        if (!updateData.containsKey("id")) {
            errors.put("id", "Id is empty.");
            return;
        }

        if (!(updateData.get("id") instanceof String)) {
            errors.put("id", "Id isn't text.");
            return;
        }

        if (!Objects.equals(bitPayId.value(), updateData.get("id"))) {
            errors.put("id", "Id not equal.");
        }
    }

    private void validateString(
        @NonNull final Map<?, ?> validationData,
        @NonNull final String fieldName,
        @NonNull final Integer maxLength,
        @NonNull final Map<String, String> errors
    ) {
        if (!validationData.containsKey(fieldName)) {
            return;
        }

        if (!(validationData.get(fieldName) instanceof String)) {
            errors.put(fieldName, StringUtils.capitalize(fieldName) + " isn't text.");
            return;
        }

        if (((String) validationData.get(fieldName)).length() > maxLength) {
            errors.put(fieldName, StringUtils.capitalize(fieldName) + " is too long.");
        }
    }
}
