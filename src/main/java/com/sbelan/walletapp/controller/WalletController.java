package com.sbelan.walletapp.controller;

import com.sbelan.walletapp.exception.DebitProcessingException;
import com.sbelan.walletapp.model.api.Balance;
import com.sbelan.walletapp.model.api.CreditRequest;
import com.sbelan.walletapp.model.api.CreditResponse;
import com.sbelan.walletapp.model.api.DebitRequest;
import com.sbelan.walletapp.model.api.DebitResponse;
import com.sbelan.walletapp.model.api.Transactions;
import com.sbelan.walletapp.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;


@Slf4j
@RestController
@RequestMapping("/wallet/api/v1")
@Tag(name = "wallet controller", description = "wallet controller for maintaining user operations")
public class WalletController {

    private WalletService walletService;

    @PostMapping("/credit")
    @Operation(summary = "Credit operation")
    public ResponseEntity<CreditResponse> credit(@RequestBody CreditRequest request) {
        log.info("Calling /wallet/api/v1/credit, request is {}", request.toString());

        CreditResponse creditResponse;
        try {
            creditResponse = walletService.creditProcessing(request);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CreditResponse.builder().responseMessage(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("Calling /wallet/api/v1/credit, response is {}", creditResponse.toString());
        return ResponseEntity.ok(creditResponse);
    }

    @PostMapping("/debit")
    @Operation(summary = "Debit operation")
    public ResponseEntity<DebitResponse> debit(@RequestBody DebitRequest request) {
        log.info("Calling /wallet/api/v1/debit, request is {}", request.toString());

        DebitResponse debitResponse;
        try {
            debitResponse = walletService.debitProcessing(request);
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DebitResponse.builder().responseMessage(e.getMessage()).build());
        } catch (DebitProcessingException e) {
            log.info("Debit processing exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DebitResponse.builder().responseMessage(e.getMessage()).build());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("Calling /wallet/api/v1/debit, response is {}", debitResponse.toString());
        return ResponseEntity.ok(debitResponse);
    }

    @GetMapping("/balance/{userId}")
    @Operation(summary = "Get user balance")
    public ResponseEntity<Balance> balance(@PathVariable Long userId) {
        try {
            Balance balance = walletService.getBalance(userId);

            log.info("Calling /wallet/api/v1/balance/{}, response is {}", userId, balance.toString());
            return ResponseEntity.ok(balance);
        } catch (EntityNotFoundException e) {
            log.error("User {} didn't found!", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get user transaction history")
    public ResponseEntity<Transactions> transactionHistory(@PathVariable Long userId) {
        Transactions transactions = walletService.getTransactionHistory(userId);

        log.info("Calling /wallet/api/v1/history/{}, response is {}", userId, transactions.toString());
        return ResponseEntity.ok(transactions);
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }
}


