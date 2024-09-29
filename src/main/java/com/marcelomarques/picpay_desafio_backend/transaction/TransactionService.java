package com.marcelomarques.picpay_desafio_backend.transaction;

import org.springframework.stereotype.Service;

import com.marcelomarques.picpay_desafio_backend.wallet.Wallet;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private Transaction save;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public Transaction creTransaction(Transaction transaction) {

        var newTransaction = transactionRepository.save(transaction);

        var wallet = walletRepository.findById(transaction.payer()).get();

        walletRepository.save(wallet.debit(transaction.value()));

        return  newTransaction;
    }
    
