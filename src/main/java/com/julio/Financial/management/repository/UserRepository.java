package com.julio.Financial.management.repository;

import com.julio.Financial.management.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
}
