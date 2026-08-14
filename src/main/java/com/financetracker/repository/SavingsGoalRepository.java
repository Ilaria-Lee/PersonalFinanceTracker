// FILE: src/main/java/com/financetracker/repository/SavingsGoalRepository.java
package com.financetracker.repository;

import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByUser(User user);
}
