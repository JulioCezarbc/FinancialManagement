package com.julio.Financial.management.infra.security;

import com.julio.Financial.management.domain.enumerated.Role;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLOutput;
import java.util.Optional;

@Configuration
public class AdminUserConfig implements CommandLineRunner {


    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserConfig(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Optional<User> userAdmin = repository.findByEmail("adminuser@gmail.com");

        userAdmin.ifPresentOrElse(
                user ->{
                    System.out.println("Admin já existe");
                },
                ()->{
                    User admin = new User();
                    admin.setFirstName("Admin");
                    admin.setLastName("User");
                    admin.setEmail("adminuser@gmail.com");
                    admin.setPassword(passwordEncoder.encode("admin12"));
                    admin.setRole(Role.ADMIN);
                    repository.save(admin);
                    System.out.println("Admin criado!");
                }
        );
    }
}
