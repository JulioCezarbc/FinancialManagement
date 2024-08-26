package com.julio.Financial.management.repository;


import com.julio.Financial.management.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    @DisplayName("Success in getting user with email")
     void findByEmailSuccess(){
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");

        repository.save(user);

        Optional<User> result = this.repository.findByEmail(user.getEmail());

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Unable to get user by email")
    void findByEmailError(){
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");

        Optional<User> result = this.repository.findByEmail(user.getEmail());

        assertThat(result.isEmpty()).isTrue();

    }

}