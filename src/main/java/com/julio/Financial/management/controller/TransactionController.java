package com.julio.Financial.management.controller;

import com.julio.Financial.management.DTO.SummaryDTO;
import com.julio.Financial.management.DTO.TransactionDTO;
import com.julio.Financial.management.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "FindAll", description = "This method fetches a list of all transactions associated with the users")
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> findAll(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(transactionService.findAll(token));
    }
    @Operation(summary = "FindById", description = "This method fetches of transactions by the id")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> findById(@PathVariable UUID id){
        return ResponseEntity.ok(transactionService.findById(id));
    }
    @Operation(summary = "FindByDate", description = "This method retrieves transactions within a specified date range")
    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getSummary(@RequestParam String start, @RequestParam String end, @RequestHeader("Authorization") String token ){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        return ResponseEntity.ok(transactionService.calculateSummary(startDate,endDate,token));
    }
    @Operation(summary = "Creates a new transaction", description = "This method creates a new transaction")
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionDTO, token));
    }
    @Operation(summary = "Updates an existing transaction", description = "This method updates a transaction identified by the ID")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable UUID id, @RequestBody TransactionDTO transactionDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDTO, token));
    }
    @Operation(summary = "Deletes an existing transaction", description = "This method deletes the transaction identified by the ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id, @RequestHeader("Authorization") String token){
        transactionService.deleteTransaction(id, token);
        return ResponseEntity.noContent().build();
    }
}
