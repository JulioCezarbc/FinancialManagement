package com.julio.Financial.management.controller;

import com.julio.Financial.management.DTO.SummaryDTO;
import com.julio.Financial.management.DTO.TransactionDTO;
import com.julio.Financial.management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getSummary(@RequestParam String start, @RequestParam String end, @RequestHeader("Authorization") String token ){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        return ResponseEntity.ok(transactionService.calculateSummary(startDate,endDate,token));
    }
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionDTO, token));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable UUID id, @RequestBody TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDTO, token));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id, @RequestHeader("Authorization") String token){
        transactionService.deleteTransaction(id, token);
        return ResponseEntity.noContent().build();
    }
}
