package com.julio.Financial.management.service;

import com.julio.Financial.management.DTO.SummaryDTO;
import com.julio.Financial.management.DTO.TransactionDTO;
import com.julio.Financial.management.domain.enumerated.Role;
import com.julio.Financial.management.domain.enumerated.TransactionType;
import com.julio.Financial.management.domain.transaction.Transaction;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.exceptions.PermissionDenied;
import com.julio.Financial.management.exceptions.TransactionNotFound;
import com.julio.Financial.management.repository.TransactionRepository;
import com.julio.Financial.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;
    @InjectMocks
    private TransactionService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;

    private User adminUser;
    private User user;

    private Transaction newTransaction;
    private Transaction tr1;
    private Transaction tr2;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        tr1 = new Transaction();
        tr1 = createTransaction(user);

        tr2 = new Transaction();
        tr2 = createTransaction(adminUser);

    }

    @Test
    @DisplayName("FindAll admin success")
    void findAllAdminSuccess(){

        when(tokenService.validateToken(anyString())).thenReturn(adminUser.getEmail());
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));

        List<Transaction> transactions = List.of(tr1, tr2);

        when(repository.findAll()).thenReturn(transactions);

        List<TransactionDTO> result = service.findAll("Bearer token");

        assertEquals(2,result.size());
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByUserEmail(anyString());
    }

    @Test
    @DisplayName("FindAll user success")
    void findAllUserSuccess(){
        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        List<Transaction> transactions = Collections.singletonList(tr1);

        when(repository.findByUserEmail("user@example.com")).thenReturn(transactions);

        List<TransactionDTO> result = service.findAll("Bearer token");

        assertEquals(1,result.size());
        verify(repository, times(1)).findByUserEmail("user@example.com");
        verify(repository, never()).findAll();

    }

    @Test
    @DisplayName("Find by id success")
    void findByIdSuccess(){
        UUID id = tr1.getId();

        when(repository.findById(id)).thenReturn(Optional.of(tr1));

        TransactionDTO result = service.findById(id);

        assertNotNull(result);
        assertEquals(tr1.getTimestamp(),result.timestamp());
        assertEquals(tr1.getType(),result.type());
        assertEquals(tr1.getUser().getEmail(),result.email());
        verify(repository,times(1)).findById(id);
    }
    @Test
    @DisplayName("Id not found")
    void findByIdError(){
        UUID id = tr1.getId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFound.class, () -> service.findById(id));
        verify(repository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Calculate summary")
    void calculateSummarySuccess(){
        LocalDate startDate = LocalDate.of(2024,8,20);
        LocalDate endDate = LocalDate.of(2024,8,30);

        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Transaction income = new Transaction();
        income.setType(TransactionType.INCOME);
        income.setAmount(new BigDecimal("100.00"));

        Transaction expense = new Transaction();
        expense.setType(TransactionType.EXPENSE);
        expense.setAmount(new BigDecimal("50.00"));


        when(repository.findByTimestamp(user.getEmail(),startDate,endDate)).thenReturn(Arrays.asList(income,expense));

        SummaryDTO summary = service.calculateSummary(startDate,endDate, "Bearer token");

        assertEquals(new BigDecimal("100.00"), summary.totalIncome());
        assertEquals(new BigDecimal("50.00"), summary.totalExpense());
    }
    @Test
    @DisplayName("Create Transaction")
    void createTransactionSuccess(){
        TransactionDTO transactionDTO = new TransactionDTO(
                TransactionType.EXPENSE,
                "pix",
                new BigDecimal("300"),
                "Pix pra",
                LocalDate.now(),
                user.getEmail()
                );
        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setType(transactionDTO.type());
        savedTransaction.setPayment(transactionDTO.payment());
        savedTransaction.setAmount(transactionDTO.amount());
        savedTransaction.setDescription(transactionDTO.description());
        savedTransaction.setTimestamp(transactionDTO.timestamp());
        savedTransaction.setUser(user);

        when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDTO result = service.createTransaction(transactionDTO, "Bearer token");

        assertNotNull(result);
        assertEquals(transactionDTO.type(), result.type());
        assertEquals(transactionDTO.payment(), result.payment());
        assertEquals(transactionDTO.amount(), result.amount());
        assertEquals(transactionDTO.description(), result.description());
        assertEquals(transactionDTO.timestamp(), result.timestamp());
        assertEquals(user.getEmail(), result.email());
        verify(repository, times(1)).save(any(Transaction.class));
    }
    @Test
    @DisplayName("Update Transaction")
    void updateTransactionSuccess(){
        TransactionDTO updatedDTO = new TransactionDTO(
                TransactionType.INCOME,
                "Credit Card",
                new BigDecimal("500"),
                "Updated description",
                LocalDate.now(),
                "user@example.com"
        );

        when(repository.findById(tr1.getId())).thenReturn(Optional.of(tr1));
        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.save(tr1)).thenReturn(tr1);

        TransactionDTO result = service.updateTransaction(tr1.getId(), updatedDTO, "Bearer token");

        assertNotNull(result);
        assertEquals(updatedDTO.type(), result.type());
        assertEquals(updatedDTO.payment(), result.payment());
        assertEquals(updatedDTO.amount(), result.amount());
        assertEquals(updatedDTO.description(), result.description());
        assertEquals(updatedDTO.timestamp(), result.timestamp());
        assertEquals(user.getEmail(), result.email());
        verify(repository, times(1)).save(tr1);

    }
    @Test
    @DisplayName("Permission denied update transaction")
    void updateTransactionPermissionDenied(){
        TransactionDTO updatedDTO = new TransactionDTO(
                TransactionType.INCOME,
                "Credit Card",
                new BigDecimal("500"),
                "Updated description",
                LocalDate.now(),
                "anotheruser@example.com"
        );

        when(repository.findById(tr2.getId())).thenReturn(Optional.of(tr2));
        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(PermissionDenied.class, () -> service.updateTransaction(tr2.getId(),updatedDTO, "Bearer token"));
        verify(repository, never()).save(any(Transaction.class));
    }
    @Test
    @DisplayName("Delete transaction")
    void deleteTransactionSuccess(){
        when(repository.findById(tr1.getId())).thenReturn(Optional.of(tr1));
        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        service.deleteTransaction(tr1.getId(), "Bearer token");
        verify(repository,times(1)).deleteById(tr1.getId());
    }

    @Test
    @DisplayName("Delete denied")
    void deleteTransactionPermissionDenied() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("other@example.com");
        otherUser.setRole(Role.USER);
        tr2.setUser(otherUser);

        when(repository.findById(tr2.getId())).thenReturn(Optional.of(tr2));
        when(tokenService.validateToken(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(PermissionDenied.class, () -> service.deleteTransaction(tr2.getId(), "Bearer token"));
        verify(repository, never()).deleteById(tr2.getId());
    }

    private Transaction createTransaction(User user){
        LocalDate testDate = LocalDate.of(2024, 8, 25);
        newTransaction = new Transaction();
        newTransaction.setId(UUID.randomUUID());
        newTransaction.setType(TransactionType.INCOME);
        newTransaction.setPayment("PIX");
        newTransaction.setAmount(new BigDecimal("100.00"));
        newTransaction.setDescription("Test Transaction");
        newTransaction.setTimestamp(testDate);
        newTransaction.setUser(user);
        return newTransaction;
    }
}