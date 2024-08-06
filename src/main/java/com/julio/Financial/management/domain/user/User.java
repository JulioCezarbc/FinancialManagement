package com.julio.Financial.management.domain.user;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;



}
