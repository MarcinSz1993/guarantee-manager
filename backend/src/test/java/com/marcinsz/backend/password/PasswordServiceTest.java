package com.marcinsz.backend.password;

import com.marcinsz.backend.exception.InvalidInputException;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.kafka.ResetPasswordKafkaProducer;
import com.marcinsz.backend.mongodb.ResetPasswordDocument;
import com.marcinsz.backend.notification.NotificationPreference;
import com.marcinsz.backend.user.Role;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PasswordServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ResetPasswordKafkaProducer resetPasswordKafkaProducer;
    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void resetPasswordShouldChangeUserPasswordFromOldOneToTheNewHashedOne(){
        User user = createUser();
        String oldPassword = user.getPassword();
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.anyString())).thenAnswer(mockArguments -> "hashed" + mockArguments.getArgument(0));
        doNothing().when(resetPasswordKafkaProducer).sendMessage(Mockito.any(ResetPasswordDocument.class));

        passwordService.resetPassword(resetPasswordRequest);
        assertNotEquals(user.getPassword(), oldPassword);
       assertTrue(user.getPassword().startsWith("hashed"));
       verify(userRepository, times(1)).findByEmail(resetPasswordRequest.getEmail());
       verify(passwordEncoder, times(1)).encode(Mockito.anyString());
       verify(resetPasswordKafkaProducer, times(1)).sendMessage(Mockito.any(ResetPasswordDocument.class));
       verify(userRepository,times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void resetPasswordShouldThrowInvalidInputExceptionWithSpecifiedCommunicateWhenInputIsNull(){
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> passwordService.resetPassword(null));
        assertEquals("Reset Password Request cannot be null!", invalidInputException.getMessage());
    }


    @Test
    public void resetPasswordShouldThrowUserNotFoundWithSpecifiedCommunicateWhenUserDoesNotExist(){
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();
        resetPasswordRequest.setEmail("notexist@gmail.com");
        when(userRepository.findByEmail(createResetPasswordRequest().getEmail())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> passwordService.resetPassword(resetPasswordRequest));

        assertEquals(userNotFoundException.getMessage(), "User with email " + resetPasswordRequest.getEmail() + " not found");
        verify(passwordEncoder, never()).encode(resetPasswordRequest.getEmail());
        verify(userRepository,times(1)).findByEmail(resetPasswordRequest.getEmail());
        verify(userRepository,never()).save(any(User.class));
        verify(resetPasswordKafkaProducer,never()).sendMessage(Mockito.any(ResetPasswordDocument.class));
    }

    @Test
    public void resetPasswordShouldGeneratePasswordWhereAtLeastOneSignIsSmallLetter(){
        String smallLetters = "abcdefghijklmnouprstwxyz";
        User user = createUser();
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.any())).thenReturn("encodedGeneratedPassword");
        doNothing().when(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));

        String newPassword = passwordService.resetPassword(resetPasswordRequest);

        assertTrue(newPassword
                .chars()
                .anyMatch(value -> smallLetters.indexOf(value) >= 0));
        verify(userRepository, Mockito.times(1)).findByEmail(resetPasswordRequest.getEmail());
        verify(passwordEncoder, Mockito.times(1)).encode(Mockito.any());
        verify(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));
    }

    @Test
    public void resetPasswordShouldGeneratePasswordWhereAtLeastOneSignIsBigLetter(){
        String bigLetters = "ABCDEFGHIJKLMNOUPRSTWXYZ";
        User user = createUser();
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.any())).thenReturn("encodedGeneratedPassword");
        doNothing().when(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));

        String newPassword = passwordService.resetPassword(resetPasswordRequest);

        assertTrue(newPassword
                .chars()
                .anyMatch(value -> bigLetters.indexOf(value) >= 0));
        verify(userRepository, Mockito.times(1)).findByEmail(resetPasswordRequest.getEmail());
        verify(passwordEncoder, Mockito.times(1)).encode(Mockito.any());
        verify(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));
    }

    @Test
    public void resetPasswordShouldGeneratePasswordWhereAtLeastOneSignIsNumber(){
        String numbers = "0123456789";
        User user = createUser();
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.any())).thenReturn("encodedGeneratedPassword");
        doNothing().when(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));

        String newPassword = passwordService.resetPassword(resetPasswordRequest);

        assertTrue(newPassword
                .chars()
                .anyMatch(value -> numbers.indexOf(value) >= 0));
        verify(userRepository, Mockito.times(1)).findByEmail(resetPasswordRequest.getEmail());
        verify(passwordEncoder, Mockito.times(1)).encode(Mockito.any());
        verify(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));
    }

    @Test
    public void resetPasswordShouldGeneratePasswordWhereAtLeastOneSignIsSpecialCharacter(){
        String specialCharacters = "!@#$%^&*()_+";
        User user = createUser();
        boolean hasSpecialSign = false;
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.any())).thenReturn("encodedGeneratedPassword");
        doNothing().when(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));

        String newPassword = passwordService.resetPassword(resetPasswordRequest);
        for (int i = 0; i < newPassword.length(); i++) {
            for (int j = 0; j < specialCharacters.length(); j++) {
                if(newPassword.charAt(i) == specialCharacters.charAt(j)){
                    hasSpecialSign = true;
                    break;
                }
            }
        }
        assertTrue(hasSpecialSign);
        verify(userRepository, Mockito.times(1)).findByEmail(resetPasswordRequest.getEmail());
        verify(passwordEncoder, Mockito.times(1)).encode(Mockito.any());
        verify(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));
    }

    @Test
    public void resetPasswordShouldSendProperDataToKafkaWhenIsOk(){
        User user = createUser();
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();

        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.any())).thenReturn("encodedGeneratedPassword");

        passwordService.resetPassword(resetPasswordRequest);

        ArgumentCaptor<ResetPasswordDocument> resetPasswordDocumentArgumentCaptor = ArgumentCaptor.forClass(ResetPasswordDocument.class);
        verify(resetPasswordKafkaProducer).sendMessage(resetPasswordDocumentArgumentCaptor.capture());

        ResetPasswordDocument captorValue = resetPasswordDocumentArgumentCaptor.getValue();

        assertEquals(user.getEmail(), captorValue.getEmail());
        assertEquals(user.getFirstName(),captorValue.getFirstName());
        assertEquals(user.getLastName(),captorValue.getLastName());
        assertEquals(user.getEmail(),captorValue.getEmail());
        assertEquals(user.getCreatedDate().toLocalDate(),captorValue.getAccountCreationDate());
    }

    @Test
    public void resetPasswordShouldGenerateNewPasswordWhenIsOk(){
        User user = createUser();
        ResetPasswordRequest resetPasswordRequest = createResetPasswordRequest();

        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encodedGeneratedPassword");
        doNothing().when(resetPasswordKafkaProducer).sendMessage(any(ResetPasswordDocument.class));

        String result = passwordService.resetPassword(resetPasswordRequest);

        assertEquals(8, result.length());
        verify(userRepository, times(1)).findByEmail(resetPasswordRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(Mockito.any());
        verify(resetPasswordKafkaProducer, times(1)).sendMessage(Mockito.any(ResetPasswordDocument.class));
        verify(userRepository, times(1)).save(Mockito.any(User.class));

    }

    private ResetPasswordRequest createResetPasswordRequest(){
        return ResetPasswordRequest.builder()
                .email("resetPasswordRequest@test.pl")
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
                .createdDate(LocalDateTime.of(2025,5,1,12,15))
                .userEnabled(false)
                .build();
    }
}
