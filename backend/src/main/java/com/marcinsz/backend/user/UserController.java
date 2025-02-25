package com.marcinsz.backend.user;

import com.marcinsz.backend.response.AuthenticationResponse;
import com.marcinsz.backend.response.RegistrationResponse;
import com.marcinsz.backend.response.UserActivationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/activate-user",produces = "application/json")
    public ResponseEntity<UserActivationResponse> activateUser(@RequestParam String userActivationToken){
        userService.activateUser(userActivationToken);
        return ResponseEntity.ok().body(
                UserActivationResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Congratulations, you have been activated. From now you can log in.")
                        .build());
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody @Valid CreateUserRequest registerRequest
    ){
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @PostMapping(value = "/login",produces = "application/json")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest loginRequest
    ){
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
