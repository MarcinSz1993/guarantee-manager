package com.marcinsz.backend.user;

import com.marcinsz.backend.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody CreateUserRequest registerRequest
    ){
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @GetMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest loginRequest
    ){
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
