package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserServiceTestMailConfig.class)
@Transactional
class RoleManagementIntegrationTest {

    private static final BCryptPasswordEncoder ENC = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void promoteModerator_returnsUpdatedUser() throws Exception {
        User target = new User("rolepromo1", "rolepromo1@test.com", ENC.encode("Password12"));
        target.setEmailVerified(true);
        target.setRole(Role.USER);
        target = userRepository.save(target);

        Map<String, String> body = Map.of(
                "adminIdentifier", "ADMIN",
                "adminPassword", "ADMIN");

        mockMvc.perform(post("/user/{userId}/promote-moderator", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MODERATOR"));

        User reloaded = userRepository.findById(target.getId()).orElseThrow();
        assertEquals(Role.MODERATOR, reloaded.getRole());
    }

    @Test
    void demoteModerator_returnsUserRole() throws Exception {
        User target = new User("roledemote1", "roledemote1@test.com", ENC.encode("Password12"));
        target.setEmailVerified(true);
        target.setRole(Role.MODERATOR);
        target = userRepository.save(target);

        Map<String, String> body = Map.of(
                "adminIdentifier", "ADMIN",
                "adminPassword", "ADMIN");

        mockMvc.perform(post("/user/{userId}/demote-moderator", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));

        User reloaded = userRepository.findById(target.getId()).orElseThrow();
        assertEquals(Role.USER, reloaded.getRole());
    }

    @Test
    void promoteModerator_rejectsWrongAdminPassword() throws Exception {
        User target = new User("rolepromo2", "rolepromo2@test.com", ENC.encode("Password12"));
        target.setEmailVerified(true);
        target = userRepository.save(target);

        Map<String, String> body = Map.of(
                "adminIdentifier", "ADMIN",
                "adminPassword", "wrong-password");

        mockMvc.perform(post("/user/{userId}/promote-moderator", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
