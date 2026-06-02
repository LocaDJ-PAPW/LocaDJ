package com.LocaDj.controller.api;

import com.LocaDj.models.User;
import com.LocaDj.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email já cadastrado"));
        }


        user.setRole(User.Role.CLIENT);
        User savedUser = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.listAll());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(java.security.Principal principal) {
        return userService.findByEmail(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sync-firebase")
    public ResponseEntity<?> syncFirebaseUsers() {
        try {
            com.google.firebase.auth.ListUsersPage page = com.google.firebase.auth.FirebaseAuth.getInstance().listUsers(null);
            int count = 0;
            for (com.google.firebase.auth.ExportedUserRecord userRecord : page.iterateAll()) {
                if (userRecord.getEmail() != null && userService.findByEmail(userRecord.getEmail()).isEmpty()) {
                    User newUser = new User();
                    newUser.setEmail(userRecord.getEmail());
                    newUser.setName(userRecord.getDisplayName() != null && !userRecord.getDisplayName().isEmpty() ? userRecord.getDisplayName() : "Usuário Firebase");
                    newUser.setPassword("firebase_sync_default");
                    newUser.setRole(User.Role.CLIENT);
                    userService.saveUser(newUser);
                    count++;
                }
            }
            return ResponseEntity.ok(Map.of("message", "Usuários sincronizados com sucesso", "novos_usuarios", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}