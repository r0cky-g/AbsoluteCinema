package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.dto.LoginRequest;
import ca.yorku.eecs4314group12.user.dto.UserResponseDTO;
import ca.yorku.eecs4314group12.user.model.Watchlist;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WatchlistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginAndGetWatchlist() throws Exception {
        // Step 1: Login with username "absolutecinema" and password "absoluteciname"
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("absolutecinema");
        loginRequest.setPassword("absoluteciname");

        MvcResult loginResult = mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Parse login response to get user ID
        String loginResponseJson = loginResult.getResponse().getContentAsString();
        UserResponseDTO loginResponse = objectMapper.readValue(loginResponseJson, UserResponseDTO.class);
        Long userId = loginResponse.getId();

        assertNotNull(userId, "User ID should not be null after login");
        System.out.println("=== LOGIN SUCCESS ===");
        System.out.println("User ID: " + userId);
        System.out.println("Username: " + loginResponse.getUsername());
        System.out.println("Email: " + loginResponse.getEmail());
        System.out.println("Role: " + loginResponse.getRole());
        System.out.println("Email Verified: " + loginResponse.isEmailVerified());

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
