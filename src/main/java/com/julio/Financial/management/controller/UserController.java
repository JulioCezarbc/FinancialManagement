package com.julio.Financial.management.controller;

import com.julio.Financial.management.DTO.UserDTO;
import com.julio.Financial.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "FindAll", description = "This method fetches a list of all users")
    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }
    @Operation(summary = "FindById", description = "This method fetches of user by the id")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.findById(id));
    }
    @Operation(summary = "FindByEmail", description = "This method searches for a user based on the provided email address")
    @GetMapping("/email")
    public ResponseEntity<UserDTO> findByEmail(@RequestParam String email){
        UserDTO user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }
    @Operation(summary = "Updates an existing user account", description = "This method updates the details of a user identified by the ID")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO user){
        return ResponseEntity.ok(userService.updateUser(id,user));
    }
    @Operation(summary = "Deletes a user account", description = "This method removes the user identified by the ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
