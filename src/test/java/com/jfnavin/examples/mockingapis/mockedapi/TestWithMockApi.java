package com.jfnavin.examples.mockingapis.mockedapi;

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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is an example of a unit / module test that mocks API calls at the HTTP
 * level with a mocked API that encapsulates the knowledge about how to stub
 * the resources and operations of the API.
 * <p>
 * This offers the same benefits as the "mocked calls within tests" approach, but adds the
 * benefit of raising the level of abstraction within the tests.
 * <p>
 * The tests now deal with API operation responses rather than with low-level HTTP calls.
 * This makes the test logic easier to grok, and makes it easier to refactor mocks in one location
 * rather than across dozens of tests.
 *
 * @see MockUserGroupApi
 */
public class TestWithMockApi {

    @RegisterExtension
    static MockUserGroupApi api = new MockUserGroupApi();

    private MyService service;

    @BeforeEach
    public void setup() {
        final var client = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(UserGroupsClient.class, api.baseUrl());

        this.service = new MyService(client);
    }

    @Test
    public void listUsernamesInGroup_returnsUserIds_whenNonEmptyGroup() {
        // Setup
        api.groupResource()
                .getGroup()
                .returnsSuccessWith(
                        new Group(1000, "group_1000", List.of(
                                new User(101, "user101", "user101@example.com"),
                                new User(102, "user102", "user102@example.com")
                        ))
                );

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).contains("user101", "user102");
    }

    @Test
    public void listUsernamesInGroup_returnsEmptyList_whenEmptyGroup() {
        // Setup
        api.groupResource()
                .getGroup()
                .returnsSuccessWith(
                        new Group(1000, "group_1000", List.of())
                );

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).isEmpty();
    }

    @Test
    public void listUsernamesInGroup_returnsEmptyList_whenUnknownGroup() {
        // Setup
        api.groupResource().getGroup().returnsNotFound(1000);

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).isEmpty();
    }

}
