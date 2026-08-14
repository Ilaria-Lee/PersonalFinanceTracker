// FILE: src/main/java/com/financetracker/repository/SavingsContributionRepository.java
package com.financetracker.repository;

import com.financetracker.model.SavingsContribution;
import com.financetracker.model.SavingsGoal;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SavingsContributionRepository extends JpaRepository<SavingsContribution, Long> {

    List<SavingsContribution> findByGoal(SavingsGoal goal);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM SavingsContribution c WHERE c.goal = :goal")
    BigDecimal sumAmountByGoal(@Param("goal") SavingsGoal goal);
}
