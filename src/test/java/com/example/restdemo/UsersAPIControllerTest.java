package com.example.restdemo;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testCreateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"2000-01-01\",\"address\":\"123 Main St\",\"phoneNumber\":\"1234567890\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("User Created Successfully"));
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"3\",\"email\":\"invalid-email\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"2000-01-01\",\"address\":\"123 Main St\",\"phoneNumber\":\"1234567890\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateUserUnderAge() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"2\",\"email\":\"test2@example.com\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"birthdate\":\"2020-01-01\",\"address\":\"456 Oak St\",\"phoneNumber\":\"9876543210\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("User must be at least 18 years old to register."));
    }

    @Test
    @Order(2)
    public void testSearchUsersByBirthdateRange() throws Exception {
        LocalDate from = LocalDate.parse("1990-01-01");
        LocalDate to = LocalDate.parse("2005-01-01");

        mockMvc.perform(MockMvcRequestBuilders.get("/users/search")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }


    @Test
    public void testSearchUsersByInvalidDateRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/search?from=2000-01-01&to=1990-01-01"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(2)
    public void testUpdateUser() throws Exception {
        //update email
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"email\":\"updated@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"2000-01-01\",\"address\":\"123 Main St\",\"phoneNumber\":\"1234567890\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Updated Successfully"));
    }

    @Test
    @Order(2)
    public void testUpdateUserFields() throws Exception {
        //update firstName and secondName
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Smith\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Fields Updated Successfully"));
    }

    @Test
    public void testUpdateUserFieldsWithInvalidUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/100/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Smith\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(2)
    public void testUpdateUserFieldsWithInvalidFields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalidField\":\"value\"}"))
                // Verifying if the response status is OK (200) - expecting that the unknown field will be ignored
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Fields Updated Successfully"));
    }

    @Test
    @Order(2)
    public void testUpdateUserFieldsWithEmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1/update-fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                //expecting that the empty field will be ignored
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Fields Updated Successfully"));
    }

    @Test
    @Order(2)
    public void testUpdateAllUserFields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"email\":\"allupdated@example.com\",\"firstName\":\"Jessica\",\"lastName\":\"Karol\",\"birthdate\":\"1995-01-01\",\"address\":\"789 Elm St\",\"phoneNumber\":\"9876543210\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Updated Successfully"));
    }

    @Test
    public void testUpdateNonExistingUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"100\",\"email\":\"updated@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"2000-01-01\",\"address\":\"456 Oak St\",\"phoneNumber\":\"1234567890\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Deleted Successfully"));
    }

    @Test
    public void testDeleteNotExistingUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/5"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}