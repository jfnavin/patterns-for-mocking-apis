package com.jfnavin.examples.mockingapis.client;

/**
 * A single (not GDPR friendly...) user object
 *
 * @param id        The user ID - unique to a user
 * @param username  The username - unique to a user
 * @param email     The user's email
 */
public record User(
        long id,
        String username,
        String email
) {
}
