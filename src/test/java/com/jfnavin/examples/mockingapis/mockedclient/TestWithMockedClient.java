package com.jfnavin.examples.mockingapis.mockedclient;

import com.jfnavin.examples.mockingapis.client.Group;
import com.jfnavin.examples.mockingapis.client.User;
import com.jfnavin.examples.mockingapis.client.UserGroupsClient;
import com.jfnavin.examples.mockingapis.service.MyService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is an example of a unit / module test that mocks the API client and uses that to drive
 * the unit tests for the service.
 * <p>
 * This has the benefit of allowing us to control exactly what the client returns, which can be valuable
 * if the client is performing complex logic like data transformation or pagination, but it decouples
 * us from the remote system API which opens the possibility of incorrect mocking that won't be revealed
 * until we get to production.
 * <p>
 * In this example we use <a href="https://github.com/mockito/mockito">mockito</a> to mock the client.
 */
@ExtendWith(MockitoExtension.class)
public class TestWithMockedClient {

    @Mock
    private UserGroupsClient client;

    @InjectMocks
    private MyService service;

    @Test
    public void listUsernamesInGroup_returnsUserIds_whenNonEmptyGroup() {
        // Setup
        when(client.getGroup(eq(1000L))).thenReturn(
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
        when(client.getGroup(eq(1000L))).thenReturn(
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

        // Note the need to mock the Feign Exception class because it doesn't give us a nice way to construct one
        // This is actually an antipattern - see https://github.com/mockito/mockito/wiki/How-to-write-good-tests#dont-mock-a-type-you-dont-own
        doThrow(mock(FeignException.NotFound.class)).when(client).getGroup(eq(1000L));

        // Execute
        final var result = service.listUsernamesInGroup(1000);

        // Assertions
        assertThat(result).isEmpty();
    }

}
