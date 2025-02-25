package com.marcinsz.backend.user;

import com.marcinsz.backend.config.JwtService;
import com.marcinsz.backend.exception.IncorrectLoginOrPasswordException;
import com.marcinsz.backend.exception.UserNotActivatedException;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.response.AuthenticationResponse;
import com.marcinsz.backend.response.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserActivationTokenService userActivationTokenService;

    public RegistrationResponse register(CreateUserRequest createUserRequest) {
        User user = User.builder()
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .role(Role.USER)
                .userEnabled(false)
                .build();
        userRepository.save(user);
        UserActivationToken userActivationToken = userActivationTokenService.createUserActivationToken(user);
        log.info(userActivationToken.getToken());
        return RegistrationResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Registration successful. Please check your email and activate your account.")
                .build();
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.getUserEmail()));
        ensureUserIsActivated(user);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IncorrectLoginOrPasswordException("Invalid username or password");
        } catch (DisabledException e) {
            throw new UserNotActivatedException();
        } catch (LockedException e) {
            throw new LockedException("User is locked", e);
        }

        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public void activateUser(String token){
        User user = userActivationTokenService.validateUserActivationTokenAndGetUser(token);
        user.setUserEnabled(true);
        userRepository.save(user);
        log.info(String.format("Activated user: %s, using token %s", user,token));
    }

    private void ensureUserIsActivated(User user){
        if (!user.isUserEnabled()){
            throw new UserNotActivatedException();
        }
    }
}
