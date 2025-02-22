package com.marcinsz.backend.user;

import com.marcinsz.backend.config.JwtService;
import com.marcinsz.backend.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(CreateUserRequest createUserRequest) {
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
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserEmail(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByEmail(loginRequest.getUserEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
