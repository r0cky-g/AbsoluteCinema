package ca.yorku.eecs4314group12.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //username set up
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 8, max = 16,
            message = "Username must be between 8 and 16 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, numbers, and underscores"
    )
    @Column(unique = true, nullable = false)
    private String username;

    //email setup
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    //password setup
    @NotBlank(message = "Password cannot be blank")
    @JsonIgnore   // Prevent password from being returned in API responses
    @Column(nullable = false)
    private String password;

    //date of birth setup
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    //user's role setup
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    //email verification
    @Pattern(regexp = "^[0-9]{4}$",
            message = "Verification code must be exactly 4 digits")
    private String verificationCode;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean over18 = false;

    // user's liked genres
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_liked_genres",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "genre")
    private Set<String> likedGenres = new HashSet<>();

    


    //constructors
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }

    //Getters
    public Long getId() { 
        return id; 
    }
    public String getUsername() { 
        return username; 
    }
    public String getEmail() { 
        return email; 
    }
    public LocalDate getDob() { 
        return dob; 
    }
    public Role getRole() { 
        return role; 
    }
    public boolean isEmailVerified() { 
        return emailVerified; 
    }
    public boolean isOver18() { 
        return over18; 
    }
    public String getVerificationCode() { 
        return verificationCode; 
    }
    public String getPassword() { 
        return password; 
    }

    public Set<String> getLikedGenres() {
        return likedGenres;
    }

    //setters
    public void setId(Long id) { 
        this.id = id;
    }
    public void setUsername(String username) { 
        this.username = username; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
    public void setDob(LocalDate dob) { 
        this.dob = dob; 
    }
    public void setRole(Role role) { 
        this.role = role; 
    }
    public void setEmailVerified(boolean emailVerified) { 
        this.emailVerified = emailVerified; 
    }
    public void setOver18(boolean over18) { 
        this.over18 = over18; 
    }
    public void setVerificationCode(String verificationCode) { 
        this.verificationCode = verificationCode; 
    }
    public void setLikedGenres(Set<String> likedGenres) {
        this.likedGenres = likedGenres;
    }
}