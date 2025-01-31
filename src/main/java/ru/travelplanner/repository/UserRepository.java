package ru.travelplanner.repository;

import org.springframework.data.repository.CrudRepository;
import ru.travelplanner.model.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
