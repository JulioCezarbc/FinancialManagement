package com.julio.Financial.management.service;

import com.julio.Financial.management.DTO.UserDTO;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.exceptions.EmailAlreadyInUse;
import com.julio.Financial.management.exceptions.UserNotFoundException;
import com.julio.Financial.management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public List<UserDTO> findAll(){
        List<User> users = repository.findAll();
        return users.stream().map(user -> new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail())).toList();
    }
    public UserDTO findById(UUID id){
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
    }
    public UserDTO findByEmail(String email){
        User user = repository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User userUpdate = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        Optional<User> existingUser = repository.findByEmail(userDTO.email());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new EmailAlreadyInUse();
        }

        userUpdate.setFirstName(userDTO.firstName());
        userUpdate.setLastName(userDTO.lastName());
        userUpdate.setEmail(userDTO.email());

        userUpdate = repository.save(userUpdate);

        return new UserDTO(
                userUpdate.getFirstName(),
                userUpdate.getLastName(),
                userUpdate.getEmail()
        );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteUser (UUID id){
        if (!repository.existsById(id)){
            throw new UserNotFoundException();
        }
        repository.deleteById(id);
    }
}