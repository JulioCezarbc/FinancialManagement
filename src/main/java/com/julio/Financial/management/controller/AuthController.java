package com.julio.Financial.management.controller;

import com.julio.Financial.management.DTO.LoginUserDTO;
import com.julio.Financial.management.DTO.RegisterUserDTO;
import com.julio.Financial.management.DTO.ResponseDTO;
import com.julio.Financial.management.domain.enumerated.Role;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.service.TokenService;
import com.julio.Financial.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginUserDTO loginUser) {
        User user = userRepository.findByEmail(loginUser.email()).orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(loginUser.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getFirstName(), token));
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterUserDTO registerUser) {
        Optional<User> user = userRepository.findByEmail(registerUser.email());

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setFirstName(registerUser.firstName());
            newUser.setLastName(registerUser.lastName());
            newUser.setEmail(registerUser.email());
            newUser.setPassword(passwordEncoder.encode(registerUser.password()));
            newUser.setRole(Role.USER);
            userRepository.save(newUser);

            String token = tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getFirstName(), token));
        }
        return ResponseEntity.badRequest().build();
    }
}
