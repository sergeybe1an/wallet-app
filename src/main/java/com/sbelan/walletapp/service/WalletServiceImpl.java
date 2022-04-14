package com.sbelan.walletapp.service;

import com.sbelan.walletapp.exception.DebitProcessingException;
import com.sbelan.walletapp.model.api.Balance;
import com.sbelan.walletapp.model.api.CreditRequest;
import com.sbelan.walletapp.model.api.CreditResponse;
import com.sbelan.walletapp.model.api.DebitRequest;
import com.sbelan.walletapp.model.api.DebitResponse;
import com.sbelan.walletapp.model.api.Transaction;
import com.sbelan.walletapp.model.api.Transactions;
import com.sbelan.walletapp.model.dto.TransactionDto;
import com.sbelan.walletapp.model.dto.TransactionType;
import com.sbelan.walletapp.model.dto.UserDto;
import com.sbelan.walletapp.repository.TransactionRepository;
import com.sbelan.walletapp.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    @Transactional
    @Override
    public CreditResponse creditProcessing(CreditRequest creditRequest) {
        UserDto user;
        try {
            user = userRepository.getById(creditRequest.getUserId());
            user.setBalance(user.getBalance().add(creditRequest.getSum()));
            userRepository.save(user);
        } catch (EntityNotFoundException e) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, String.format("User %d didn't found!", creditRequest.getUserId()));
        }

        TransactionDto transaction = TransactionDto.builder()
            .sum(creditRequest.getSum())
            .date(LocalDateTime.now())
            .type(TransactionType.CREDIT)
            .userId(user.getId())
            .build();

        transaction = transactionRepository.save(transaction);

        return CreditResponse.builder()
            .balance(user.getBalance())
            .responseMessage(String.format("Success transaction #%d for user %s", transaction.getId(), creditRequest.getUserId()))
            .build();
    }

    @Transactional
    @Override
    public DebitResponse debitProcessing(DebitRequest debitRequest)
        throws DebitProcessingException {

        UserDto user;
        try {
            user = userRepository.getById(debitRequest.getUserId());

            if (debitRequest.getSum().compareTo(user.getBalance()) > 0) {
                BigDecimal diff = debitRequest.getSum().subtract(user.getBalance());
                throw new DebitProcessingException(String.format("Failed transaction for user %s, please replenish your balance atleast on %s", debitRequest.getUserId(), diff));
            }
        } catch (EntityNotFoundException e) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, String.format("User %d didn't found!", debitRequest.getUserId()));
        }

        TransactionDto transaction = TransactionDto.builder()
            .sum(debitRequest.getSum())
            .date(LocalDateTime.now())
            .type(TransactionType.DEBIT)
            .userId(user.getId())
            .build();

        transaction = transactionRepository.save(transaction);

        user.setBalance(user.getBalance().subtract(debitRequest.getSum()));
        userRepository.save(user);

        return DebitResponse.builder()
            .balance(user.getBalance())
            .responseMessage(String.format("Success transaction #%d for user %s", transaction.getId(), debitRequest.getUserId()))
            .build();
    }

    @Transactional
    @Override
    public Balance getBalance(Long userId) {
        UserDto user = userRepository.getById(userId);

        return Balance.builder()
            .userId(userId)
            .balance(user.getBalance())
            .build();
    }

    @Transactional
    @Override
    public Transactions getTransactionHistory(Long userId) {
        List<TransactionDto> transactions = transactionRepository.findAllByUserId(userId);

        if (transactions.isEmpty()) {
            return Transactions.builder().transactions(Collections.emptyList()).build();
        }

        List<Transaction> transactionsHistory = transactions.stream()
            .map(transactionDto ->
                Transaction.builder()
                    .id(transactionDto.getId())
                    .userId(userId)
                    .type(transactionDto.getType())
                    .sum(transactionDto.getSum())
                    .date(transactionDto.getDate())
                    .build())
            .collect(Collectors.toList());

        return Transactions.builder()
            .transactions(transactionsHistory)
            .build();
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
