package org.example.redis_pr2.service;

import lombok.RequiredArgsConstructor;
import org.example.redis_pr2.entity.User;
import org.example.redis_pr2.payload.user.CreateUserRequest;
import org.example.redis_pr2.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisCacheService redisCacheService;

    public User getUserById(Long id) {
        String cacheKey = "user:" + id;
        User cachedUser = (User) redisCacheService.get(cacheKey);

        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("User with id [%s] not found", id)));

        redisCacheService.save(cacheKey, user);

        return user;
    }

    public User createUser(CreateUserRequest request) {
        User user = User.builder()
                .name(request.name())
                .password(request.password())
                .build();

        User savedUser = userRepository.save(user);
        redisCacheService.save("user:" + savedUser.getId(), savedUser);
        return savedUser;
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        redisCacheService.delete("user:" + id);
    }
}

