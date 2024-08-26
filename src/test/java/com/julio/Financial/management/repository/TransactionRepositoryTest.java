package com.julio.Financial.management.repository;

import com.julio.Financial.management.domain.enumerated.TransactionType;
import com.julio.Financial.management.domain.transaction.Transaction;
import com.julio.Financial.management.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Success in finding transactions by user email")
    void findByUserEmailSuccess(){

        User user = createUser();

        Transaction transaction = createTransaction(user.getEmail());

        List<Transaction> transactions = repository.findByUserEmail(user.getEmail());

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(transaction.getType(), transactions.getFirst().getType());
    }

    @Test
    @DisplayName("Error in finding transactions by user email")
    void findByUserEmailError(){
        User user = createUser();

        List<Transaction> transactions = repository.findByUserEmail(user.getEmail());

        assertEquals(0, transactions.size());
    }

    @Test
    void findTransactionTimestampSuccess(){
        User user = createUser();

        LocalDate start = LocalDate.of(2024, 8, 20);
        LocalDate end = LocalDate.of(2024, 8, 27);

        Transaction transaction = createTransaction(user.getEmail());

        List<Transaction> transactions = repository.findByTimestamp(user.getEmail(),start,end);

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(transaction.getId(), transactions.getFirst().getId());
    }

    @Test
    void findTransactionTimestampError(){
        User user = createUser();

        LocalDate start = LocalDate.of(2024, 8, 20);
        LocalDate end = LocalDate.of(2024, 8, 27);


        List<Transaction> transactions = repository.findByTimestamp(user.getEmail(),start,end);
        assertEquals(0, transactions.size());

    }

    private User createUser(){
        User user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("test");
        user.setLastName("user");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Transaction createTransaction(String email) {
        User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

        LocalDate testDate = LocalDate.of(2024, 8, 25);
        Transaction newTransaction = new Transaction(TransactionType.INCOME, "pix", new BigDecimal(10),
                "description", testDate, user);

        entityManager.persist(newTransaction);
        entityManager.flush();
        return newTransaction;
    }
}
