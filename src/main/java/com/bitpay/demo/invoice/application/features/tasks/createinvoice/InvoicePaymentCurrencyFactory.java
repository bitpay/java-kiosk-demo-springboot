/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.invoice.application.features.tasks.createinvoice;

import com.bitpay.demo.DependencyInjection;
import com.bitpay.demo.invoice.domain.CurrencyCode;
import com.bitpay.demo.invoice.domain.payment.DisplaySubtotal;
import com.bitpay.demo.invoice.domain.payment.DisplayTotal;
import com.bitpay.demo.invoice.domain.payment.Enabled;
import com.bitpay.demo.invoice.domain.payment.FiatAmount;
import com.bitpay.demo.invoice.domain.payment.InvoicePayment;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrency;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrencyCode;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrencyExchangeRate;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrencyMinerFee;
import com.bitpay.demo.invoice.domain.payment.InvoicePaymentCurrencySupportedTransactionCurrency;
import com.bitpay.demo.invoice.domain.payment.PaymentCode;
import com.bitpay.demo.invoice.domain.payment.PaymentCodeUrl;
import com.bitpay.demo.invoice.domain.payment.Rate;
import com.bitpay.demo.invoice.domain.payment.Reason;
import com.bitpay.demo.invoice.domain.payment.SatoshisPerByte;
import com.bitpay.demo.invoice.domain.payment.Subtotal;
import com.bitpay.demo.invoice.domain.payment.Total;
import com.bitpay.demo.invoice.domain.payment.TotalFee;
import com.bitpay.sdk.model.invoice.MinerFees;
import com.bitpay.sdk.model.invoice.MinerFeesItem;
import com.bitpay.sdk.model.invoice.SupportedTransactionCurrencies;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.NonNull;

@DependencyInjection
class InvoicePaymentCurrencyFactory {

    @NonNull
    InvoicePaymentCurrency create(
        @NonNull final Map.Entry<String, String> code,
        @NonNull final InvoicePayment invoicePayment,
        @NonNull final com.bitpay.sdk.model.invoice.Invoice bitPayInvoice
    ) {
        final var bitPaySupportedTransactionCurrency = getBitPaySupportedTransactionCurrency(
            code.getKey(),
            bitPayInvoice.getSupportedTransactionCurrencies()
        );

        final var bitPayMinerFee = getBitPayMinerFee(
            code.getKey(),
            bitPayInvoice.getMinerFees()
        );

        final var invoicePaymentTotal = new InvoicePaymentCurrency(
            invoicePayment,
            new CurrencyCode(code.getKey()),
            new Total(code.getValue()),
            new Subtotal(bitPayInvoice.getPaymentSubTotals().getOrDefault(code.getKey(), null)),
            new DisplayTotal(bitPayInvoice.getPaymentDisplayTotals().getOrDefault(code.getKey(), null)),
            new DisplaySubtotal(bitPayInvoice.getPaymentDisplaySubTotals().getOrDefault(code.getKey(), null)),
            getSupportedTransactionCurrency(bitPaySupportedTransactionCurrency),
            getMinerFee(bitPayMinerFee)
        );

        invoicePaymentTotal.addExchangeRates(
            getExchangeRates(
                invoicePaymentTotal,
                bitPayInvoice.getExchangeRates().getOrDefault(code.getKey(), new Hashtable<>())
            )
        );
        invoicePaymentTotal.addPaymentCodes(
            getPaymentCodes(
                invoicePaymentTotal,
                bitPayInvoice.getPaymentCodes().getOrDefault(code.getKey(), new Hashtable<>())
            )
        );

        return invoicePaymentTotal;
    }


    private Collection<InvoicePaymentCurrencyCode> getPaymentCodes(
        @NonNull final InvoicePaymentCurrency invoicePaymentTotal,
        @NonNull final Map<String, String> paymentCodes
    ) {
        return paymentCodes.entrySet().stream()
            .map(paymentCode ->
                new InvoicePaymentCurrencyCode(
                    invoicePaymentTotal,
                    new PaymentCode(paymentCode.getKey()),
                    new PaymentCodeUrl(paymentCode.getValue())
                )
            )
            .toList();
    }

    private Collection<InvoicePaymentCurrencyExchangeRate> getExchangeRates(
        @NonNull final InvoicePaymentCurrency invoicePaymentTotal,
        @NonNull final Map<String, String> exchangeRates
    ) {
        return exchangeRates.entrySet().stream()
            .map(exchangeRate ->
                new InvoicePaymentCurrencyExchangeRate(
                    invoicePaymentTotal,
                    new CurrencyCode(exchangeRate.getKey()),
                    new Rate(exchangeRate.getValue())
                )
            )
            .toList();
    }

    @Nullable
    private MinerFeesItem getBitPayMinerFee(
        @NonNull final String code,
        @NonNull final MinerFees minerFees
    ) {
        return switch (code.toLowerCase()) {
            case "gusd" -> minerFees.getGusd();
            case "btc" -> minerFees.getBtc();
            case "usdc" -> minerFees.getUsdc();
            case "eth" -> minerFees.getEth();
            case "bch" -> minerFees.getBch();
            case "pax" -> minerFees.getPax();
            default -> null;
        };
    }

    @Nullable
    private com.bitpay.sdk.model.invoice.SupportedTransactionCurrency getBitPaySupportedTransactionCurrency(
        @NonNull final String code,
        @NonNull final SupportedTransactionCurrencies supportedTransactionCurrencies
    ) {
        return switch (code.toLowerCase()) {
            case "gusd" -> supportedTransactionCurrencies.getGusd();
            case "btc" -> supportedTransactionCurrencies.getBtc();
            case "usdc" -> supportedTransactionCurrencies.getUsdc();
            case "eth" -> supportedTransactionCurrencies.getEth();
            case "bch" -> supportedTransactionCurrencies.getBch();
            case "pax" -> supportedTransactionCurrencies.getPax();
            default -> null;
        };
    }

    @NonNull
    private InvoicePaymentCurrencyMinerFee getMinerFee(@Nullable final MinerFeesItem bitPayMinerFee) {
        if (Objects.isNull(bitPayMinerFee)) {
            return new InvoicePaymentCurrencyMinerFee();
        }

        return new InvoicePaymentCurrencyMinerFee(
            new SatoshisPerByte(bitPayMinerFee.getSatoshisPerByte().doubleValue()),
            new TotalFee(bitPayMinerFee.getTotalFee().doubleValue()),
            new FiatAmount(bitPayMinerFee.getFiatAmount())
        );
    }

    @NonNull
    private InvoicePaymentCurrencySupportedTransactionCurrency getSupportedTransactionCurrency(
        @Nullable final com.bitpay.sdk.model.invoice.SupportedTransactionCurrency bitPaySupportedTransactionCurrency
    ) {
        if (Objects.isNull(bitPaySupportedTransactionCurrency)) {
            return new InvoicePaymentCurrencySupportedTransactionCurrency(
                new Enabled(false),
                null
            );
        }

        return new InvoicePaymentCurrencySupportedTransactionCurrency(
            new Enabled(bitPaySupportedTransactionCurrency.getEnabled()),
            new Reason(bitPaySupportedTransactionCurrency.getReason())
        );
    }
}
