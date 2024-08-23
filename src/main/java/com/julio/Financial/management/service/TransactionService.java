package com.julio.Financial.management.service;

import com.julio.Financial.management.DTO.SummaryDTO;
import com.julio.Financial.management.DTO.TransactionDTO;
import com.julio.Financial.management.domain.transaction.Transaction;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.repository.TransactionRepository;
import com.julio.Financial.management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;


    public List<TransactionDTO> findAll(String token){
        User user = getUserFromToken(token);

        List<Transaction> transactions;

        if (user.getRole().name().equalsIgnoreCase("admin")){
            transactions = repository.findAll();
        }else {
            transactions = repository.findByUserEmail(user.getEmail());
        }
        return transactions.stream().map(transaction -> new TransactionDTO(transaction.getType(), transaction.getPayment(), transaction.getAmount(),
                transaction.getDescription(),transaction.getTimestamp(),transaction.getUser().getEmail())).toList();

    }

    public TransactionDTO findById(UUID id){
        Transaction transaction =repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found transaction with id : " + id));
        return new TransactionDTO(transaction.getType(), transaction.getPayment(), transaction.getAmount(),
                transaction.getDescription(),transaction.getTimestamp(),transaction.getUser().getEmail());
    }

    public SummaryDTO calculateSummary(LocalDate startDate, LocalDate endDate, String token) {
        User user = getUserFromToken(token);

        List<Transaction> transactions = repository.findByTimestamp(user.getEmail(), startDate, endDate);


        BigDecimal income = transactions.stream()
                .filter(transaction -> transaction.getType().name().equalsIgnoreCase("income"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);



        BigDecimal expense = transactions.stream()
                .filter(transaction -> transaction.getType().name().equalsIgnoreCase("expense"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SummaryDTO(income, expense);
    }


    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO,String token){
        Transaction transaction = new Transaction();
        transaction.setType(transactionDTO.type());
        transaction.setPayment(transactionDTO.payment());
        transaction.setAmount(transactionDTO.amount());
        transaction.setDescription(transactionDTO.description());
        transaction.setTimestamp(transactionDTO.timestamp());

        User user = getUserFromToken(token);

        transaction.setUser(user);

        repository.save(transaction);

        return new TransactionDTO(
                transaction.getType(),
                transaction.getPayment(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTimestamp(),
                user.getEmail()
        );

    }

    @Transactional
    public TransactionDTO updateTransaction(UUID uuid, TransactionDTO transactionDTO,String token){
        Transaction transactionUpdate = repository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("Transaction with id: " + uuid + " not found"));

        transactionUpdate.setType(transactionDTO.type());
        transactionUpdate.setPayment(transactionDTO.payment());
        transactionUpdate.setAmount(transactionDTO.amount());
        transactionUpdate.setDescription(transactionDTO.description());
        transactionUpdate.setTimestamp(transactionDTO.timestamp());

        User user = getUserFromToken(token);

        transactionUpdate.setUser(user);

        transactionUpdate = repository.save(transactionUpdate);

        return new TransactionDTO(
                transactionUpdate.getType(),
                transactionUpdate.getPayment(),
                transactionUpdate.getAmount(),
                transactionUpdate.getDescription(),
                transactionUpdate.getTimestamp(),
                transactionUpdate.getUser().getEmail()
        );
    }

    @Transactional
    public void deleteTransaction(UUID id,String token){
        Transaction transaction = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Transaction with id: " + id + " not found"));

        User user = getUserFromToken(token);

        if (!transaction.getUser().getEmail().equals(user.getEmail()) &&
                !transaction.getUser().getRole().name().equalsIgnoreCase("admin")) {
            throw new SecurityException("You do not have permission to delete this transaction");
        }
        repository.deleteById(id);
    }

    private User getUserFromToken(String token) {
        String jwtToken = token.replace("Bearer ", "");
        String email = tokenService.validateToken(jwtToken);
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}
