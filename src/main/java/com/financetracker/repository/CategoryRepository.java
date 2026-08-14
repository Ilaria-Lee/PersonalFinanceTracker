// FILE: src/main/java/com/financetracker/repository/CategoryRepository.java
package com.financetracker.repository;

import com.financetracker.model.Category;
import com.financetracker.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    Optional<Category> findByNameAndUser(String name, User user);

    boolean existsByIdAndUser_Id(Long id, Long userId);
}
