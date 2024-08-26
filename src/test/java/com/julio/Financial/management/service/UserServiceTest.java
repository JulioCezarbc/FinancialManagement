package com.julio.Financial.management.service;

import com.julio.Financial.management.DTO.UserDTO;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.exceptions.EmailAlreadyInUse;
import com.julio.Financial.management.exceptions.UserNotFoundException;
import com.julio.Financial.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;
    private UserDTO userDTO;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setFirstName("test");
        user.setLastName("user");
        user.setEmail("test@example.com");


        userDTO = new UserDTO("test", "user", "test@example.com");

    }

    @Test
    @DisplayName("Find all users")
    void findAllSuccess(){
        User user1 = new User();
        user1.setFirstName("example");
        user1.setLastName("user");
        user1.setEmail("user@example.com");
        user1.setPassword("12345");

        when(repository.findAll()).thenReturn(List.of(user, user1));
        List<UserDTO> users = userService.findAll();

        assertEquals(2,users.size());

        assertEquals("test", users.getFirst().firstName());
        assertEquals("user", users.getFirst().lastName());
        assertEquals("test@example.com", users.getFirst().email());

        assertEquals("example", users.get(1).firstName());
        assertEquals("user", users.get(1).lastName());
        assertEquals("user@example.com", users.get(1).email());

    }

    @Test
    @DisplayName("Success Update")
    void updateUserSuccess(){
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        UserDTO updateUser = userService.updateUser(userId,userDTO);

        assertNotNull(updateUser);
        assertEquals(userDTO.firstName(), updateUser.firstName());
        assertEquals(userDTO.lastName(), updateUser.lastName());
        assertEquals(userDTO.email(), updateUser.email());

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Success delete")
    void deleteUserSuccess(){
        UUID userId = UUID.randomUUID();

        when(repository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);

        verify(repository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Error delete")
    void deleteUserError(){
        UUID userId = UUID.randomUUID();
        when(repository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(repository, never()).deleteById(userId);
    }

    @Test
    @DisplayName("Get user with email")
    void findByEmailSuccess(){
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserDTO userDTO = userService.findByEmail(user.getEmail());

        verify(repository,times(1)).findByEmail(user.getEmail());

        assertNotNull(userDTO);
        assertEquals(user.getFirstName(), userDTO.firstName());
        assertEquals(user.getEmail(), userDTO.email());
    }
    @Test
    @DisplayName("Error get user with email")
    void findByEmailError(){
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            userService.findByEmail(user.getEmail());
        });
        assertEquals("User not found", thrownException.getMessage());
    }
    @Test
    @DisplayName("data conflict")
    void updateUser_EmailAlreadyInUse(){
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setEmail("test@example.com");

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(anotherUser));

        assertThrows(EmailAlreadyInUse.class, () ->{
            userService.updateUser(userId,userDTO);
        });
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User not found")
    void updateUser_NotFound(){
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->{
            userService.updateUser(userId,userDTO);
        });
        verify(repository, never()).save(any(User.class));
    }
}