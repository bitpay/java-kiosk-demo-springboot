/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo.shared.infrastructure;

import com.bitpay.demo.shared.bitpayproperties.BitPayProperties;
import lombok.NonNull;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
class CustomErrorViewResolver {

    private final BitPayProperties bitPayProperties;

    CustomErrorViewResolver(@NonNull final BitPayProperties bitPayProperties) {
        this.bitPayProperties = bitPayProperties;
    }

    @ModelAttribute
    public void addDesignAttribute(Model model) {
        model.addAttribute("design", this.bitPayProperties.getDesign());
    }
}
