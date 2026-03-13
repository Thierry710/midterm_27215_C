package auca.ac.rw.onlineVotingSystem.service;

import auca.ac.rw.onlineVotingSystem.enums.UserRole;
import auca.ac.rw.onlineVotingSystem.model.User;
import auca.ac.rw.onlineVotingSystem.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepo.existsByUsername(username);
    }

    public User registerUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.valueOf(role.toUpperCase()));
        user.setHasVoted(false);
        return userRepo.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public long countVoters() {
        return userRepo.countByRole(UserRole.VOTER);
    }

    public long countVoted() {
        return userRepo.countByHasVotedTrue();
    }

    public Page<User> getVotersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        return userRepo.findByRole(UserRole.VOTER, pageable);
    }
}
