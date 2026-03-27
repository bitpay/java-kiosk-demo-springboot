/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.infrastructure.ui.createinvoice;

import com.bitpay.demo.shared.logger.LogCode;
import com.bitpay.demo.shared.logger.Logger;
import com.bitpay.sdk.exceptions.BitPayException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import lombok.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice("com.bitpay.demo.invoice.infrastructure.ui.createinvoice")
public class CreateInvoiceExceptionHandler {

    private final Logger logger;

    public CreateInvoiceExceptionHandler(@NonNull final Logger logger) {
        this.logger = logger;
    }

    @ExceptionHandler(BitPayException.class)
    public String handleBitPayException(
        @NonNull final BitPayException exception,
        @NonNull final RedirectAttributes redirectAttrs
    ) {
        logException(exception);
        redirectAttrs.addFlashAttribute("error", exception.getMessage());

        return "redirect:";
    }

    @ExceptionHandler(Throwable.class)
    public String handleAnyException(
        @NonNull final Throwable exception,
        @NonNull final RedirectAttributes redirectAttrs
    ) {
        logException(exception);
        redirectAttrs.addFlashAttribute("error", exception.getMessage());

        return "redirect:";
    }

    private void logException(@NonNull final Throwable exception) {
        final LinkedList<String> stackTrace = new LinkedList<>();
        Arrays.stream(exception.getStackTrace()).forEach(
            stackTraceElement -> stackTrace.addLast(stackTraceElement.toString())
        );

        this.logger.error(
            LogCode.INVOICE_CREATE_FAIL,
            "Failed to create invoice",
            Map.of(
                "errorMessage", exception.getMessage(),
                "stackTrace", stackTrace
            )
        );
    }
}
