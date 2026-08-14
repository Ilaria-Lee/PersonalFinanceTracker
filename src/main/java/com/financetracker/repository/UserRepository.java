// FILE: src/main/java/com/financetracker/repository/UserRepository.java
package com.financetracker.repository;

import com.financetracker.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
