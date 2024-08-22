package com.julio.Financial.management.service;

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


    public List<TransactionDTO> findAll(@RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", "");
        String email = tokenService.validateToken(jwtToken);

        User user = userRepository.findByEmail(email).orElseThrow( ()-> new EntityNotFoundException("User not found"));
        List<Transaction> transactions;

        if (user.getRole().name().equalsIgnoreCase("admin")){
            transactions = repository.findAll();
        }else {
            transactions = repository.findByUserEmail(email);
        }
        return transactions.stream().map(transaction -> new TransactionDTO(transaction.getType(), transaction.getPayment(), transaction.getAmount(),
                transaction.getDescription(),transaction.getTimestamp(),transaction.getUser().getEmail())).toList();

    }

    public TransactionDTO findById(UUID id){
        Transaction transaction =repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found transaction with id : " + id));
        return new TransactionDTO(transaction.getType(), transaction.getPayment(), transaction.getAmount(),
                transaction.getDescription(),transaction.getTimestamp(),transaction.getUser().getEmail());
    }

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        Transaction transaction = new Transaction();
        transaction.setType(transactionDTO.type());
        transaction.setPayment(transactionDTO.payment());
        transaction.setAmount(transactionDTO.amount());
        transaction.setDescription(transactionDTO.description());
        transaction.setTimestamp(transactionDTO.timestamp());

        String jwtToken = token.replace("Bearer ", "");
        String email = tokenService.validateToken(jwtToken);

        User user = userRepository.findByEmail(email).orElseThrow( ()-> new EntityNotFoundException("User not found"));
        transaction.setUser(user);

        repository.save(transaction);

        return new TransactionDTO(
                transaction.getType(),
                transaction.getPayment(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTimestamp(),
                email
        );

    }

    @Transactional
    public TransactionDTO updateTransaction(UUID uuid, TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        Transaction transactionUpdate = repository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("Transaction with id: " + uuid + " not found"));

        transactionUpdate.setType(transactionDTO.type());
        transactionUpdate.setPayment(transactionDTO.payment());
        transactionUpdate.setAmount(transactionDTO.amount());
        transactionUpdate.setDescription(transactionDTO.description());
        transactionUpdate.setTimestamp(transactionDTO.timestamp());
        String jwtToken = token.replace("Bearer ", "");
        String email = tokenService.validateToken(jwtToken);

        User user = userRepository.findByEmail(email).orElseThrow( ()-> new EntityNotFoundException("User not found"));
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
    public void deleteTransaction(UUID id){
        if (!repository.existsById(id)){
            throw new EntityNotFoundException("Transaction with id: " + id + " not found");
        }
        repository.deleteById(id);
    }
}
