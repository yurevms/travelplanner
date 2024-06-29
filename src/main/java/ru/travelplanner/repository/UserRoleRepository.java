package ru.travelplanner.repository;

import org.springframework.data.repository.CrudRepository;
import ru.travelplanner.model.UserRole;
import java.util.List;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
