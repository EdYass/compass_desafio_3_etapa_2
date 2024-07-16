package com.EdYass.ecommerce.repository;

import com.EdYass.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByResetToken(String resetToken);
    boolean existsByEmail(String email);
}
