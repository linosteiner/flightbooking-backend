package ch.linosteiner.auth;

import ch.linosteiner.repository.UserRepository;
import io.micronaut.core.annotation.Blocking;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

@Singleton
public class BasicAuthenticationProvider<B> implements HttpRequestAuthenticationProvider<B> {

    private final PasswordHasher hasher;
    private final UserRepository userRepository;

    public BasicAuthenticationProvider(
            PasswordHasher hasher,
            UserRepository userRepository
    ) {
        this.hasher = hasher;
        this.userRepository = userRepository;
    }

    @Override
    @Blocking
    public @NonNull AuthenticationResponse authenticate(
            HttpRequest<B> requestContext,
            @NonNull AuthenticationRequest<String, String> authRequest
    ) {
        final String username = authRequest.getIdentity();
        final String password = authRequest.getSecret();

        return userRepository.findByUsername(username)
                .filter(user -> hasher.matches(password, user.getPasswordHash()))
                .map(user -> AuthenticationResponse.success(user.getUsername()))
                .orElseGet(AuthenticationFailed::new);
    }
}
