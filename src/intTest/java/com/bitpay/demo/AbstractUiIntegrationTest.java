/*
 * Copyright 2023 BitPay.
 * All rights reserved.
 */

package com.bitpay.demo;

import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractUiIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    public void initialize() {
        prepareMocMvc();
    }

    protected ResultActions get(final String url) throws Exception {
        return getResultActions(MockMvcRequestBuilders.get(url));
    }

    protected ResultActions post(
        final String url,
        final Map<String, Object> requestBody
    ) throws Exception {
        final var post = MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        requestBody.forEach((key, value) -> post.param(key, value.toString()));

        return getResultActions(post);
    }

    protected ResultActions post(
        final String url,
        final String requestBody,
        @Nullable final Map<String, String> headers
    ) throws Exception {
        final var post = MockMvcRequestBuilders.post(url).content(requestBody)
            .contentType(MediaType.APPLICATION_JSON);

        if (!Objects.isNull(headers)) {
            headers.forEach((key, value) -> post.header(key, value));
        }

        return getResultActions(post);
    }

    private void prepareMocMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    private ResultActions getResultActions(
        final MockHttpServletRequestBuilder mockHttpServletRequestBuilder
    ) throws Exception {
        return this.mockMvc.perform(mockHttpServletRequestBuilder);
    }
}
