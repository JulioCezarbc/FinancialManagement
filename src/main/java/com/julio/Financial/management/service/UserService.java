package com.julio.Financial.management.service;

import com.julio.Financial.management.DTO.LoginUserDTO;
import com.julio.Financial.management.DTO.RegisterUserDTO;
import com.julio.Financial.management.DTO.UserDTO;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    private User user;

    public List<UserDTO> findAll(){
        List<User> users = repository.findAll();
        return users.stream().map(user -> new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail())).toList();
    }
    public UserDTO findById(UUID id){
        User user = repository.findById(id).orElseThrow(() ->new EntityNotFoundException("User with id: " + id + " not found"));
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
    }
    public UserDTO findByEmail(String email){
        User user = repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(("User with email: " + email + " not found")));
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public User registerUser(RegisterUserDTO registerUser) {
        User user = new User();
        user.setFirstName(registerUser.firstName());
        user.setLastName(registerUser.lastName());
        user.setEmail(registerUser.email());
        user.setPassword(registerUser.password());

        return repository.save(user);
    }

    public User loginUser(LoginUserDTO loginUser) {
        User user = repository.findByEmail(loginUser.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!loginUser.password().equals(user.getPassword())) {
            throw new IllegalArgumentException("User with credentials incorrect");
        }
        return user;
    }

    public User updateUser (UUID id, UserDTO userDTO){
        User userUpdate = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found"));

        userUpdate.setFirstName(userDTO.firstName());
        userUpdate.setLastName(userDTO.lastName());
        userUpdate.setEmail(userDTO.email());
        return repository.save(userUpdate);
    }

    public void deleteUser (UUID id){
        if (!repository.existsById(id)){
            throw new EntityNotFoundException("User with id: " + id + " not found");
        }
        repository.deleteById(id);
    }
}