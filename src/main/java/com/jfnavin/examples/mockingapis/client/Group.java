package com.jfnavin.examples.mockingapis.client;

import java.util.List;

/**
 * A single group that contains 0 or more {@link User}s
 *
 * @param id    The group ID
 * @param name  The display name of the group
 * @param users The users in the group
 */
public record Group(
        long id,
        String name,
        List<User> users
) {
}
