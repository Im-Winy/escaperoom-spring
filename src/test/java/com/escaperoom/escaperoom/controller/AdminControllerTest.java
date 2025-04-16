package com.escaperoom.escaperoom.controller;

import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService; // Assure-toi que ce service existe pour créer des utilisateurs

    @BeforeEach
    public void setup() {
        // Tu peux configurer les utilisateurs ici, ou dans une méthode séparée si nécessaire
    	userService.register("John", "Doe", "adminUser", "admin@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simule un utilisateur avec le rôle ROLE_ADMIN
    public void testAdminAccess_withAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin"))
               .andExpect(status().isOk()); // Vérifie que l'accès est autorisé
    }

    @Test
    @WithMockUser(roles = "USER") // Simule un utilisateur avec le rôle ROLE_USER
    public void testAdminAccess_withUserRole() throws Exception {
        mockMvc.perform(get("/api/admin"))
               .andExpect(status().isForbidden()); // Vérifie que l'accès est interdit
    }

    @Test
    public void testAdminAccess_withoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin"))
               .andExpect(status().isUnauthorized()); // Vérifie que l'accès sans authentification est refusé
    }
}
