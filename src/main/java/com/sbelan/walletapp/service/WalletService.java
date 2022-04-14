package com.sbelan.walletapp.service;

import com.sbelan.walletapp.exception.DebitProcessingException;
import com.sbelan.walletapp.model.api.Balance;
import com.sbelan.walletapp.model.api.CreditRequest;
import com.sbelan.walletapp.model.api.CreditResponse;
import com.sbelan.walletapp.model.api.DebitRequest;
import com.sbelan.walletapp.model.api.DebitResponse;
import com.sbelan.walletapp.model.api.Transactions;

public interface WalletService {

    CreditResponse creditProcessing(CreditRequest creditRequest);

    DebitResponse debitProcessing(DebitRequest debitRequest) throws DebitProcessingException;

    Balance getBalance(Long userId);

    Transactions getTransactionHistory(Long userId);
}
