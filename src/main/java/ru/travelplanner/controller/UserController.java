package ru.travelplanner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.travelplanner.model.UserAuthority;
import ru.travelplanner.model.UserRole;
import ru.travelplanner.service.UserService;
import ru.travelplanner.model.User;
import ru.travelplanner.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("registration")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("")
    public ResponseEntity<Void> register(@RequestParam String username, @RequestParam String password) {
        userService.register(username, password);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addRole(
            @RequestParam("userId") Long userId,
            @RequestParam("role") UserAuthority authority) {
        userService.addRole(userId, authority);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeRole(
            @RequestParam("userId") Long userId,
            @RequestParam("role") UserAuthority authority) {
        userService.removeRole(userId, authority);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserRole>> getUserRoles(@PathVariable Long userId) {
        List<UserRole> roles = userService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }
}
