package org.example.redis_pr2.payload.user;

public record CreateUserRequest(
        String name,
        String password
) {
}
