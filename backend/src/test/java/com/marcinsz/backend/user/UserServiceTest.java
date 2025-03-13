package com.marcinsz.backend.user;

import com.marcinsz.backend.config.JwtService;
import com.marcinsz.backend.exception.IncorrectLoginOrPasswordException;
import com.marcinsz.backend.exception.UserAlreadyExistsException;
import com.marcinsz.backend.exception.UserNotActivatedException;
import com.marcinsz.backend.response.AuthenticationResponse;
import com.marcinsz.backend.response.RegistrationResponse;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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


    @Test
    public void loginShouldThrowIncorrectLoginOrPasswordExceptionWithSpecifiedCommunicateWhenUserIsNotFound(){
        LoginRequest loginRequest = createLoginRequest();
        when(userRepository.findByEmail(loginRequest.getUserEmail())).thenReturn(Optional.empty());
        IncorrectLoginOrPasswordException incorrectLoginOrPasswordException = assertThrows(IncorrectLoginOrPasswordException.class, () -> userService.login(loginRequest));
        assertEquals("Invalid email or password", incorrectLoginOrPasswordException.getMessage());
        Mockito.verify(authenticationManager, Mockito.never()).authenticate(any());
        Mockito.verify(jwtService, Mockito.never()).generateToken(any());
    }

    @ParameterizedTest
    @CsvSource({
            "false,false",
            "true,false",
            "false,true"
    })
    public void loginShouldThrowIncorrectLoginOrPasswordExceptionWithSpecifiedCommunicateWhenAtLeastOneCredentialIsIncorrect(
            boolean isUserEmailCorrect, boolean isPasswordCorrect) {

        LoginRequest loginRequest = createLoginRequest();
        User user = createUser();
        user.setUserEnabled(true);

        if (!isUserEmailCorrect) {
            when(userRepository.findByEmail(loginRequest.getUserEmail()))
                    .thenReturn(Optional.empty());
        } else {
            when(userRepository.findByEmail(loginRequest.getUserEmail()))
                    .thenReturn(Optional.of(user));
        }

        if (isUserEmailCorrect && !isPasswordCorrect) {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid email or password"));
        }

        IncorrectLoginOrPasswordException exception = assertThrows(IncorrectLoginOrPasswordException.class, () -> userService.login(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        Mockito.verify(userRepository).findByEmail(loginRequest.getUserEmail());

        if (isUserEmailCorrect) {
            Mockito.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        } else {
            Mockito.verify(authenticationManager, Mockito.never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }

    @Test
    public void loginShouldThrowUserNotActivatedExceptionWithSpecifiedCommunicateWhenUserIsNotActivated(){
        LoginRequest loginRequest = createLoginRequest();
        User user = createUser();
        when(userRepository.findByEmail(loginRequest.getUserEmail())).thenReturn(Optional.of(user));
        UserNotActivatedException userNotActivatedException = assertThrows(UserNotActivatedException.class, () -> userService.login(loginRequest));

        assertEquals("User is not activated", userNotActivatedException.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(loginRequest.getUserEmail());
        Mockito.verify(authenticationManager,Mockito.never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(jwtService,Mockito.never()).generateToken(user);
    }
    @Test
    public void loginShouldReturnAuthenticationResponseWhenSuccessful() {
        LoginRequest loginRequest = createLoginRequest();
        User user = createUser();
        user.setUserEnabled(true);
        String authToken = "authToken";

        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token(authToken)
                .build();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUserEmail(), loginRequest.getPassword());

        when(userRepository.findByEmail(loginRequest.getUserEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(usernamePasswordAuthenticationToken);
        when(jwtService.generateToken(user)).thenReturn(authToken);

        AuthenticationResponse actualResponse = userService.login(loginRequest);

        assertEquals(expectedResponse.getToken(), actualResponse.getToken());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(loginRequest.getUserEmail());
        Mockito.verify(jwtService, Mockito.times(1)).generateToken(user);
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @ParameterizedTest
    @CsvSource({
            "true,true",
            "false,true",
            "true,false"
    })
    public void registerShouldThrowUserAlreadyExistsExceptionWithSpecialCommunicateWheTypedUserAlreadyExists(boolean usernameExists,boolean emailExists) {
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

        Mockito.verify(userActivationTokenService).sendActivationEmail(any(User.class));
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

    public LoginRequest createLoginRequest(){
        return LoginRequest.builder()
                .userEmail("tommy@testmail.com")
                .password("qwerty")
                .build();
    }
}
