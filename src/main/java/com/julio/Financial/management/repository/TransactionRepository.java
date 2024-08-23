package com.julio.Financial.management.repository;

import com.julio.Financial.management.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserEmail(String email);

    @Query("SELECT t FROM Transaction t JOIN t.user u WHERE u.email = :email AND t.timestamp BETWEEN :startDate AND :endDate")
    List<Transaction> findByTimestamp(@Param("email") String email, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
