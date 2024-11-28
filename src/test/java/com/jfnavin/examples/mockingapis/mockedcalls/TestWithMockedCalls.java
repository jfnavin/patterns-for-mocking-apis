package com.jfnavin.examples.mockingapis.mockedcalls;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jfnavin.examples.mockingapis.client.Group;
import com.jfnavin.examples.mockingapis.client.User;
import com.jfnavin.examples.mockingapis.client.UserGroupsClient;
import com.jfnavin.examples.mockingapis.service.MyService;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is an example of a unit / module test that mocks API calls at the HTTP
 * level within test methods.
 * <p>
 * This has the benefit of testing the service + client as a unit against simulated
 * HTTP API responses, but suffers from pushing low-level information about HTTP requests
 * into the tests which really should be focusing on the business logic.
 * <p>
 * In this example we use <a href="https://wiremock.org/">wiremock</a> to mock the HTTP responses.
 */
public class TestWithMockedCalls {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private MyService service;

    @BeforeEach
    public void setup() {
        final var client = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(UserGroupsClient.class, wm.baseUrl());

        this.service = new MyService(client);
    }

    @Test
    public void listUsernamesInGroup_returnsUserIds_whenNonEmptyGroup() {
        // Setup
        final var apiResponse = new Group(1000, "group_1000", List.of(
                new User(101, "user101", "user101@example.com"),
                new User(102, "user102", "user102@example.com")
        ));
        wm.stubFor(get("/group/1000").willReturn(okForJson(apiResponse)));

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).contains("user101", "user102");
    }

    @Test
    public void listUsernamesInGroup_returnsEmptyList_whenEmptyGroup() {
        // Setup
        final var apiResponse = new Group(1000, "group_1000", List.of());
        wm.stubFor(get("/group/1000").willReturn(okForJson(apiResponse)));

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).isEmpty();
    }

    @Test
    public void listUsernamesInGroup_returnsEmptyList_whenUnknownGroup() {
        // Setup
        wm.stubFor(get("/group/1000").willReturn(notFound()));

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).isEmpty();
    }

}
