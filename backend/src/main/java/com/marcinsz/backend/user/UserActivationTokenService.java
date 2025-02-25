package com.marcinsz.backend.user;

import com.marcinsz.backend.exception.InvalidTokenException;
import com.marcinsz.backend.exception.UserActivationTokenExpiredException;
import com.marcinsz.backend.exception.UserActivationTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserActivationTokenService {

    private final UserActivationTokenRepository userActivationTokenRepository;


    @Value("${spring.application.user-activation-token-characters}")
    private String charactersToGenerateUserActivationToken;

    @Value("${spring.application.user-activation-token-length}")
    private int userActivationTokenLength;

    public UserActivationToken createUserActivationToken(User user){
        String activateToken = generateActivateToken();
        UserActivationToken userActivationToken = UserActivationToken.builder()
                .token(activateToken)
                .expires(LocalDateTime.now().plusMinutes(15))
                .validatedAt(null)
                .isValid(true)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
       return userActivationTokenRepository.save(userActivationToken);
    }

    public String generateActivateToken(){
        StringBuilder activateToken = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < userActivationTokenLength; i++) {
            int tokenCharacter = random.nextInt(charactersToGenerateUserActivationToken.length());
            activateToken.append(charactersToGenerateUserActivationToken.charAt(tokenCharacter));
        }
        return activateToken.toString().toUpperCase();
    }

    public User validateUserActivationTokenAndGetUser(String token){
        if (token.length() > userActivationTokenLength) {
            throw new InvalidTokenException("The token is too long");
        } else if (token.length() < userActivationTokenLength) {
            throw new InvalidTokenException("The token is too short");
        }
        UserActivationToken foundUserActivationToken = userActivationTokenRepository.findByToken(token).orElseThrow(() -> new UserActivationTokenNotFoundException(token));
        if (foundUserActivationToken.getExpires().isBefore(LocalDateTime.now())){
            throw new UserActivationTokenExpiredException();
        } else if (!foundUserActivationToken.isValid()) {
            throw new InvalidTokenException("The token is invalid. Already used.");
        }
        foundUserActivationToken.setValidatedAt(LocalDateTime.now());
        foundUserActivationToken.setValid(false);
        userActivationTokenRepository.save(foundUserActivationToken);
        return foundUserActivationToken.getUser();
    }
}
