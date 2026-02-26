package ca.yorku.eecs4314group12.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ca.yorku.eecs4314group12.user.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}