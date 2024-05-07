package com.example.restdemo.controller;

import com.example.restdemo.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersAPIController {

    private List<User> users = new ArrayList<>();

    @Value("${user.minAge}")
    private int minAge;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        User foundUser = findUserById(userId);
        return foundUser != null ? ResponseEntity.ok(foundUser) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByBirthdateRange(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") @NotNull LocalDate from,
                                                                  @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") @NotNull LocalDate to) {
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }
        List<User> usersInRange = new ArrayList<>();
        for (User user : users) {
            if (user.getBirthdate().isAfter(from) && user.getBirthdate().isBefore(to)) {
                usersInRange.add(user);
            }
        }
        return ResponseEntity.ok(usersInRange);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid email format or other validation errors.");
        }
        if (isUnderAge(user.getBirthdate())) {
            return ResponseEntity.badRequest().body("User must be at least " + minAge + " years old to register.");
        }
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Created Successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @Valid @RequestBody User updatedUser) {
        User user = findUserById(userId);
        if (user != null) {
            if (isUnderAge(updatedUser.getBirthdate())) {
                return ResponseEntity.badRequest().body("User must be at least " + minAge + " years old.");
            }
            updateUserFields(user, updatedUser);
            return ResponseEntity.ok("User Updated Successfully");
        } else {
            return ResponseEntity.notFound().build(); // Виправлення: повернути статус NOT_FOUND, якщо користувача не знайдено
        }
    }


    @PatchMapping("/{userId}/update-fields")
    public ResponseEntity<String> updateUserFields(@PathVariable String userId, @Valid @RequestBody Map<String, Object> fieldsToUpdate) {
        User user = findUserById(userId);
        if (user != null) {
            updateUserFields(user, fieldsToUpdate);
            return ResponseEntity.ok("User Fields Updated Successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void updateUserFields(User user, Map<String, Object> fieldsToUpdate) {
        fieldsToUpdate.forEach((field, value) -> {
            switch (field) {
                case "email":
                    user.setEmail((String) value);
                    break;
                case "firstName":
                    user.setFirstName((String) value);
                    break;
                case "lastName":
                    user.setLastName((String) value);
                    break;
                case "birthdate":
                    user.setBirthdate(LocalDate.parse((String) value));
                    break;
                case "address":
                    user.setAddress((String) value);
                    break;
                case "phoneNumber":
                    user.setPhoneNumber((String) value);
                    break;
                default:
                    break;
            }
        });
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        User user = findUserById(userId);
        if (user != null) {
            users.remove(user);
            return ResponseEntity.ok("User Deleted Successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private User findUserById(String userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst().orElse(null);
    }

    private boolean isUnderAge(LocalDate birthdate) {
        return LocalDate.now().minusYears(minAge).isBefore(birthdate);
    }

    private void updateUserFields(User user, User updatedUser) {
        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setBirthdate(updatedUser.getBirthdate());
        user.setAddress(updatedUser.getAddress());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
    }
}