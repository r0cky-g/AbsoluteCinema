package ca.yorku.eecs4314group12.user.service;

import org.springframework.stereotype.Service;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import ca.yorku.eecs4314group12.user.model.User;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createUser(User user) { return repo.save(user); }
    public List<User> getAllUsers() { return repo.findAll(); }
}
