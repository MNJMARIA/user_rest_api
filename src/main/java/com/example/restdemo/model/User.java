package com.example.restdemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;


@Validated
@Getter
@Setter
public class User {
    @NotNull
    private String id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(max = 50) // припустима максимальна довжина
    private String firstName;

    @NotNull
    @Size(max = 50) // припустима максимальна довжина
    private String lastName;

    @NotNull
    @Past
    private LocalDate birthdate;

    @Size(max = 100)
    private String address;

    @Pattern(regexp="\\d{10}")
    private String phoneNumber;


    public User(String usersId, String email, String firstName, String lastName, LocalDate birthdate, String address, String phoneNumber) {
        this.id = usersId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
