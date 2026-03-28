package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.dto.LoginRequest;
import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.model.Watchlist;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserServiceTestMailConfig.class)
@Transactional
public class WatchlistIntegrationTest {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void seedVerifiedUser() {
        String username = "wltestuser";
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User u = new User(username, "wltestuser@example.com", PASSWORD_ENCODER.encode("Wltestpass1"));
        u.setEmailVerified(true);
        u.setRole(Role.USER);
        userRepository.save(u);
    }

    @Test
    public void testLoginAndGetWatchlist() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("wltestuser");
        loginRequest.setPassword("Wltestpass1");

        MvcResult loginResult = mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String loginResponseJson = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginResponseJson);
        Long userId = loginNode.get("id").asLong();

        assertNotNull(userId, "User ID should not be null after login");
        System.out.println("=== LOGIN SUCCESS ===");
        System.out.println("User ID: " + userId);
        System.out.println("Username: " + loginNode.get("username").asText());
        System.out.println("Email: " + loginNode.get("email").asText());
        System.out.println("Role: " + loginNode.get("role").asText());
        System.out.println("Email Verified: " + loginNode.get("emailVerified").asBoolean());

        // Step 2: Get watchlist for the user
        MvcResult watchlistResult = mockMvc.perform(get("/user/{userId}/watchlist", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse watchlist response
        String watchlistJson = watchlistResult.getResponse().getContentAsString();
        Watchlist[] watchlistArray = objectMapper.readValue(watchlistJson, Watchlist[].class);
        List<Watchlist> watchlist = java.util.Arrays.asList(watchlistArray);

        System.out.println("\n=== WATCHLIST RESULTS ===");
        System.out.println("Number of items in watchlist: " + watchlist.size());
        System.out.println("\nFull watchlist JSON:");
        System.out.println(watchlistJson);
        
        System.out.println("\n=== WATCHLIST ITEMS ===");
        for (int i = 0; i < watchlist.size(); i++) {
            Watchlist item = watchlist.get(i);
            System.out.println("\nItem " + (i + 1) + ":");
            System.out.println("  ID: " + item.getId());
            System.out.println("  User ID: " + item.getUserId());
            System.out.println("  Movie ID: " + item.getMovieId());
            System.out.println("  Added At: " + item.getAddedAt());
        }

        // Assertions
        assertNotNull(watchlist, "Watchlist should not be null");
        System.out.println("\n=== TEST COMPLETED ===");
        System.out.println("Watchlist contains " + watchlist.size() + " item(s)");
    }
}
