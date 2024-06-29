package ru.travelplanner.service;

import ru.travelplanner.model.User;

import java.util.Optional;

public interface UserService {
    void register(String username, String password);

    Optional<User> findByUsername(String username);
}
