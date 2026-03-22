package ch.linosteiner.service;

import ch.linosteiner.domain.UserEntity;
import ch.linosteiner.repository.UserRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
