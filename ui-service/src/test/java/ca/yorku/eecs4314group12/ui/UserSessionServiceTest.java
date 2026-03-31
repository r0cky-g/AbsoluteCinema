package ca.yorku.eecs4314group12.ui;

import ca.yorku.eecs4314group12.ui.security.UserSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserSessionService.
 * Place in: ui-service/src/test/java/ca/yorku/eecs4314group12/ui/UserSessionServiceTest.java
 */
class UserSessionServiceTest {

    private UserSessionService userSessionService;

    @BeforeEach
    void setUp() {
        userSessionService = new UserSessionService();
    }

    // isLoggedIn() should return false before any user is set
    @Test
    void isLoggedIn_beforeSetUser_returnsFalse() {
        assertFalse(userSessionService.isLoggedIn());
    }

    // getUserId() should return null before any user is set
    @Test
    void getUserId_beforeSetUser_returnsNull() {
        assertNull(userSessionService.getUserId());
    }

    // getRole() should return "USER" as default when no role is set
    @Test
    void getRole_beforeSetUser_returnsDefaultUser() {
        assertEquals("USER", userSessionService.getRole());
    }

    // After setUser(), isLoggedIn() should return true
    @Test
    void isLoggedIn_afterSetUser_returnsTrue() {
        userSessionService.setUser(42L, "USER", "test@example.com");
        assertTrue(userSessionService.isLoggedIn());
    }

    // getUserId() should return the correct id after setUser()
    @Test
    void getUserId_afterSetUser_returnsCorrectId() {
        userSessionService.setUser(42L, "USER", "test@example.com");
        assertEquals(42L, userSessionService.getUserId());
    }

    // getRole() should return the correct role after setUser()
    @Test
    void getRole_afterSetUser_returnsCorrectRole() {
        userSessionService.setUser(1L, "ADMIN", "admin@example.com");
        assertEquals("ADMIN", userSessionService.getRole());
    }

    // getEmail() should return the correct email after setUser()
    @Test
    void getEmail_afterSetUser_returnsCorrectEmail() {
        userSessionService.setUser(1L, "USER", "user@example.com");
        assertEquals("user@example.com", userSessionService.getEmail());
    }

    // After clear(), isLoggedIn() should return false again
    @Test
    void isLoggedIn_afterClear_returnsFalse() {
        userSessionService.setUser(1L, "USER", "user@example.com");
        userSessionService.clear();
        assertFalse(userSessionService.isLoggedIn());
    }

    // After clear(), getUserId() should be null
    @Test
    void getUserId_afterClear_returnsNull() {
        userSessionService.setUser(1L, "USER", "user@example.com");
        userSessionService.clear();
        assertNull(userSessionService.getUserId());
    }

    // After clear(), getRole() should fall back to "USER" default
    @Test
    void getRole_afterClear_returnsDefault() {
        userSessionService.setUser(1L, "ADMIN", "admin@example.com");
        userSessionService.clear();
        assertEquals("USER", userSessionService.getRole());
    }

    // setUser() with null role — getRole() should still return "USER" default
    @Test
    void getRole_withNullRole_returnsDefault() {
        userSessionService.setUser(1L, null, "user@example.com");
        assertEquals("USER", userSessionService.getRole());
    }
}