package com.jfnavin.examples.mockingapis.service;

import com.jfnavin.examples.mockingapis.client.User;
import com.jfnavin.examples.mockingapis.client.UserGroupsClient;
import feign.FeignException;

import java.util.List;

public class MyService {
    private final UserGroupsClient client;

    public MyService(UserGroupsClient client) {
        this.client = client;
    }

    /**
     * This is a silly example that represents a service-layer operation that performs
     * some business logic with the response from the API client.
     */
    public List<String> listUsernamesInGroup(long groupId) {
        try {
            return client.getGroup(groupId).users().stream().map(User::username).toList();
        } catch (FeignException.NotFound nfe) {
            return List.of();
        }
    }
}
