package org.example.redis_pr2.repository;

import org.example.redis_pr2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
