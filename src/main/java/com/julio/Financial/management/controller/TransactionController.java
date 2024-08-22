package com.julio.Financial.management.controller;

import com.julio.Financial.management.DTO.TransactionDTO;
import com.julio.Financial.management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> findAll(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(transactionService.findAll(token));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> findById(@PathVariable UUID id){
        return ResponseEntity.ok(transactionService.findById(id));
    }
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(transactionService.createTransaction(transactionDTO, token));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable UUID id, @RequestBody TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDTO, token));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
