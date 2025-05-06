package com.marcinsz.backend.user;

import com.marcinsz.backend.config.JwtService;
import com.marcinsz.backend.exception.*;
import com.marcinsz.backend.notification.NotificationPreference;
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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void chooseNotificationPreferenceShouldThrowInvalidInputWithSpecifiedCommunicationWhenNotificationPreferenceIsNull(){
        User user = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,null,Collections.emptyList());

        InvalidInputException expectedException = assertThrows(InvalidInputException.class, () -> userService.chooseNotificationPreference(authentication, null));
        assertEquals("Notification preference cannot be null!", expectedException.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void chooseNotificationPreferenceShouldThrowInvalidInputWithSpecifiedCommunicationWhenPrincipalIsNotUser(){
        NotificationPreference notificationPreference = NotificationPreference.EMAIL;
        Authentication authentication = new UsernamePasswordAuthenticationToken("null",null,Collections.emptyList());

        InvalidInputException expectedException = assertThrows(InvalidInputException.class, () -> userService.chooseNotificationPreference(authentication, notificationPreference));
        assertEquals("Invalid user", expectedException.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void chooseNotificationPreferenceShouldSetUserPreferenceCorrectly(){
        NotificationPreference notificationPreference = NotificationPreference.EMAIL;
        User user = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,null,Collections.emptyList());

        System.out.println(user.getNotificationPreference());
        userService.chooseNotificationPreference(authentication,notificationPreference);

        assertEquals(user.getNotificationPreference(), notificationPreference);
        verify(userRepository,times(1)).save(user);
    }

    @Test
    public void getUserByUsernameShouldThrowUserNotFoundExceptionWhenUserDoesNotExist(){
        String notExistingUsername = "not-existing-username";
        when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

        UserNotFoundException expectedException = assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(notExistingUsername));

        assertEquals("User with username not-existing-username not found", expectedException.getMessage());
        verify(userRepository,times(1)).findByUsername(notExistingUsername);
    }

    @Test
    public void getUserByUsernameShouldReturnUserDtoSuccessfully(){
        String username = "tommy";
        User user = createUser();
        UserDto expectedUserDto = UserDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .userEmail(user.getEmail())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .notificationPreference(user.getNotificationPreference())
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDto actualUserDto = userService.getUserByUsername(username);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userRepository,times(1)).findByUsername(username);
    }

    @Test
    public void getUserByEmailShouldThrowUserNotFoundExceptionWhenUserDoesNotExist(){
        String notExistingEmail = "not-existing-email@test.pl";
        when(userRepository.findByEmail(notExistingEmail)).thenReturn(Optional.empty());

        UserNotFoundException expectedException = assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(notExistingEmail));

        assertEquals("User with email not-existing-email@test.pl not found", expectedException.getMessage());
        verify(userRepository,times(1)).findByEmail(notExistingEmail);
    }

    @Test
    public void getUserByEmailShouldReturnUserDtoSuccessfully(){
        String email = "tommy@testmail.com";
        User user = createUser();
        UserDto expectedUserDto = UserDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .userEmail(user.getEmail())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .notificationPreference(user.getNotificationPreference())
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDto actualUserDto = userService.getUserByEmail(email);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userRepository,times(1)).findByEmail(email);
    }

    @Test
    public void activateUserShouldSetUserToEnabledAndSaveWhenSuccessful() throws MessagingException {
        String token = "token";
        User user = createUser();
        when(userActivationTokenService.validateUserActivationTokenAndGetUser(token)).thenReturn(user);
        userService.activateUser(token);
        assertTrue(user.isUserEnabled());
        verify(userRepository,times(1)).save(user);
        verify(userActivationTokenService,times(1)).validateUserActivationTokenAndGetUser(token);
    }

    @Test
    public void deleteAccountShouldFailAndThrowSpecifiedExceptionWhenPrincipalIsNotUser(){
        Authentication authentication = new UsernamePasswordAuthenticationToken("string", null, Collections.emptyList());
        InvalidInputException expectedException = assertThrows(InvalidInputException.class, () -> userService.deleteAccount(authentication));
        assertEquals("Invalid user",expectedException.getMessage());
        verify(userRepository,never()).delete(any(User.class));
    }

    @Test
    public void deleteAccountShouldFailAndThrowSpecifiedExceptionWhenPrincipalIsNull(){
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, null);
        InvalidInputException expectedException = assertThrows(InvalidInputException.class, () -> userService.deleteAccount(authentication));
        assertEquals("Invalid user",expectedException.getMessage());
        verify(userRepository,never()).delete(any(User.class));
    }

    @Test
    public void deleteAccountShouldDeleteAccountSuccessfully(){
        User user1 = createUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user1,user1.getPassword(), Collections.emptyList());
        userService.deleteAccount(authentication);
        verify(userRepository,times(1)).delete(user1);
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
    public void loginShouldThrowUserNotActivatedExceptionWithSpecifiedCommunicationWhenAuthenticationManagerThrowsDisabledException(){
        User user = createUser();
        user.setUserEnabled(true);
        LoginRequest loginRequest = createLoginRequest();
        when(userRepository.findByEmail(loginRequest.getUserEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("User disabled"));
        UserNotActivatedException expectedException = assertThrows(UserNotActivatedException.class, () -> userService.login(loginRequest));
        assertEquals("User is not activated", expectedException.getMessage());
        Mockito.verify(userRepository, times(1)).findByEmail(loginRequest.getUserEmail());
        Mockito.verify(authenticationManager,times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(jwtService,Mockito.never()).generateToken(user);
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

    @Test
    public void registerShouldThrowInvalidInputExceptionWithSpecifiedCommunicateWhenConfirmPasswordFieldIsNull(){
        CreateUserRequest createUserRequest = createUserRequest();
        createUserRequest.setConfirmPassword(null);
        when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.empty());
        InvalidInputException expectedException = assertThrows(InvalidInputException.class, () -> userService.register(createUserRequest));
        assertEquals("Password not confirmed correctly.", expectedException.getMessage());
        verify(userRepository).findByEmail(createUserRequest.getEmail());
        verify(userRepository).findByUsername(createUserRequest.getUsername());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    public void registerShouldThrowInvalidInputExceptionWithSpecifiedCommunicateWhenPasswordsAreNotTheSame(){
        CreateUserRequest createUserRequest = createUserRequest();
        createUserRequest.setConfirmPassword("Not the same password");
        when(userRepository.findByEmail(createUserRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.empty());
        InvalidInputException expectedException = assertThrows(InvalidInputException.class, () -> userService.register(createUserRequest));
        assertEquals("Password not confirmed correctly.", expectedException.getMessage());
        verify(userRepository).findByEmail(createUserRequest.getEmail());
        verify(userRepository).findByUsername(createUserRequest.getUsername());
        verify(userRepository,never()).save(any(User.class));
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
                .confirmPassword("qwerty")
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
                .notificationPreference(NotificationPreference.ALL)
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
