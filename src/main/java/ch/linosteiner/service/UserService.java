package ch.linosteiner.service;

import ch.linosteiner.domain.UserEntity;
import ch.linosteiner.repository.UserRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findByUsername(String username) {
        LOG.debug("Suche User in Datenbank nach Username: {}", username);
        return userRepository.findByUsername(username);
    }
}
