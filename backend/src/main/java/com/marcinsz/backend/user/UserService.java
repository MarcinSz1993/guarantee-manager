package com.marcinsz.backend.user;

import com.marcinsz.backend.config.JwtService;
import com.marcinsz.backend.exception.IncorrectLoginOrPasswordException;
import com.marcinsz.backend.exception.UserAlreadyExistsException;
import com.marcinsz.backend.exception.UserNotActivatedException;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.notification.NotificationPreference;
import com.marcinsz.backend.response.AuthenticationResponse;
import com.marcinsz.backend.response.RegistrationResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public RegistrationResponse register(CreateUserRequest createUserRequest) throws MessagingException {
        validateNewUser(createUserRequest);
        User user = User.builder()
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .role(Role.USER)
                .notificationPreference(null)
                .userEnabled(false)
                .build();
        userRepository.save(user);
        userActivationTokenService.sendActivationEmail(user);
        return RegistrationResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Registration successful. Please check your email and activate your account.")
                .build();
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getUserEmail())
                .orElseThrow(() -> new IncorrectLoginOrPasswordException("Invalid email or password"));
        ensureUserIsActivated(user);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IncorrectLoginOrPasswordException("Invalid email or password");
        } catch (DisabledException e) {
            throw new UserNotActivatedException();
        }

        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public void activateUser(String token) throws MessagingException {
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

    private void validateNewUser(CreateUserRequest createUserRequest){
        if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent() ||
            userRepository.findByUsername(createUserRequest.getUsername()).isPresent()){
            throw new UserAlreadyExistsException(createUserRequest.getUsername(),createUserRequest.getEmail());
        }
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return UserDto.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getEmail())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .notificationPreference(user.getNotificationPreference())
                .build();
    }

    public void chooseNotificationPreference(Authentication authentication,
                                            NotificationPreference notificationPreference) {
        User user = (User) authentication.getPrincipal();
        user.setNotificationPreference(notificationPreference);
        userRepository.save(user);
    }
}
