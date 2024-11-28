package com.jfnavin.examples.mockingapis.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers({
        "Content-Type: application/json",
        "Accept: application/json"
})
public interface UserGroupsClient {

    @RequestLine("POST /user")
    User createUser(User userRequest);

    @RequestLine("GET /user/{userId}")
    User getUser(@Param("userId") long userId);

    @RequestLine("POST /group")
    Group createGroup(Group groupRequest);

    @RequestLine("GET /group/{groupId}")
    Group getGroup(@Param("groupId") long groupId);
}
