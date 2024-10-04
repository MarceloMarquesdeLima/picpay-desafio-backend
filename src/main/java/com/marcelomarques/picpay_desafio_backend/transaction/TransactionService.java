package com.marcelomarques.picpay_desafio_backend.transaction;

import org.springframework.stereotype.Service;

import com.marcelomarques.picpay_desafio_backend.transaction.Transaction;
import com.marcelomarques.picpay_desafio_backend.wallet.Wallet;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletRepository;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletType;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private Transaction save;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public Transaction create(Transaction transaction) {

        validate(transaction);

        var newTransaction = transactionRepository.save(transaction);

        var wallet = walletRepository.findById(transaction.payer()).get();

        walletRepository.save(wallet.debit(transaction.value()));

        return  newTransaction;
    }

    /**
     * @param transaction
     */
    public void validate(Transaction transaction){
        walletRepository.findById(transaction.payee())
            .map(payee -> walletRepository.findById(transaction.payer()))
            .map(payer -> payer.type() == WalletType.COMUM.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payee()) ? transaction : null);
    }
}
