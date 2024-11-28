package com.jfnavin.examples.mockingapis.mockedapivalidated;

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
 * level with a mocked API that validates its interactions against the
 * <a href="https://swagger.io/specification/">OpenAPI specification</a> for the API.
 * <p>
 * This offers the same encapsulation and level-of-abstraction benefits of the
 * "test with mocked API" approach, but adds contract testing to validate that the mocks
 * the test interacts with are syntactically correct as per the API specification.
 * <p>
 * This test uses the <a href="https://bitbucket.org/atlassian/swagger-request-validator/src/master/">swagger-request-validator</a>
 * library to do the API validation.
 *
 * @see ValidatedMockUserGroupApi
 */
public class TestWithValidatedMockApi {

    @RegisterExtension
    static ValidatedMockUserGroupApi api = new ValidatedMockUserGroupApi();

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
