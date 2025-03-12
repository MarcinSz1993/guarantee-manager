package com.marcinsz.backend.user;

import com.marcinsz.backend.config.JwtService;
import com.marcinsz.backend.exception.UserAlreadyExistsException;
import com.marcinsz.backend.response.RegistrationResponse;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserActivationTokenService userActivationTokenService;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "true,true",
            "false,true",
            "true,false"
    })
    public void registerShouldThrowUserAlreadyExistsExceptionWithSpecialCommunicateWheTypedUserAlreadyExists(boolean usernameExists,boolean emailExists) throws MessagingException {
        CreateUserRequest createUserRequest = createUserRequest();
        User user = createUser();
        if (usernameExists){
            when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.of(user));
        } else {
            when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.empty());
        }

        if (emailExists){
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.of(user));
        } else {
            when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
        }

        UserAlreadyExistsException userAlreadyExistsException = assertThrows(UserAlreadyExistsException.class, () -> userService.register(createUserRequest));
        assertEquals("User with username tommysmith or email tommy@testmail.com already exists!",userAlreadyExistsException.getMessage());
    }


    @Test
    public void registerShouldSendEmailToUserWhenSuccessfullyRegistered() throws MessagingException {
        CreateUserRequest createUserRequest = createUserRequest();
        User expectedUser = createUser();
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("hashedQwerty");
        doNothing().when(userActivationTokenService).sendActivationEmail(expectedUser);

        userService.register(createUserRequest);

        Mockito.verify(userActivationTokenService).sendActivationEmail(Mockito.any(User.class));
    }

    @Test
    public void registerShouldCreateAndSaveUserCorrectly() throws MessagingException {
        CreateUserRequest createUserRequest = createUserRequest();
        User expectedUser = createUser();
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("hashedQwerty");
        doNothing().when(userActivationTokenService).sendActivationEmail(expectedUser);

        userService.register(createUserRequest);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();

        Mockito.verify(userRepository).findByUsername(createUserRequest.getUsername());

        assertEquals(createUserRequest.getFirstName(), actualUser.getFirstName());
        assertEquals(createUserRequest.getLastName(), actualUser.getLastName());
        assertEquals(createUserRequest.getEmail(), actualUser.getEmail());
        assertEquals(createUserRequest.getUsername(),actualUser.getUsername());
        assertEquals("hashedQwerty", actualUser.getPassword());
        assertFalse(actualUser.isUserEnabled());
    }

    @Test
    public void registerShouldReturnStatusCode200AndCorrectResponseWhenSuccessful() throws MessagingException {
        CreateUserRequest createUserRequest = createUserRequest();
        User expectedUser = createUser();
        doNothing().when(userActivationTokenService).sendActivationEmail(expectedUser);

        RegistrationResponse actualResponse = userService.register(createUserRequest);
                assertEquals("Registration successful. Please check your email and activate your account.",actualResponse.getMessage());
                assertEquals(200,actualResponse.getStatusCode());

        Mockito.verify(userRepository).findByUsername(createUserRequest.getUsername());
        Mockito.verify(userRepository).findByEmail(createUserRequest.getEmail());
    }

    private CreateUserRequest createUserRequest(){
        return CreateUserRequest.builder()
                .firstName("Tommy")
                .lastName("Smith")
                .username("tommysmith")
                .password("qwerty")
                .email("tommy@testmail.com")
                .build();
    }

    private User createUser(){
        return User.builder()
                .firstName("Tommy")
                .lastName("Smith")
                .username("tommysmith")
                .password("hashedQwerty")
                .email("tommy@testmail.com")
                .role(Role.USER)
                .createdDate(null)
                .userEnabled(false)
                .build();
    }
}
