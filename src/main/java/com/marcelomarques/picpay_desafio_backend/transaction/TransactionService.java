package com.marcelomarques.picpay_desafio_backend.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcelomarques.picpay_desafio_backend.authorization.AuthorizerService;
import com.marcelomarques.picpay_desafio_backend.wallet.Wallet;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletRepository;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletType;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private Transaction save;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
    }
    
    @Transactional
    public Transaction create(Transaction transaction) {

        validate(transaction);

        var newTransaction = transactionRepository.save(transaction);

        var wallet = walletRepository.findById(transaction.payer()).get();

        walletRepository.save(wallet.debit(transaction.value()));

        authorizerService.authorize(transaction);

        return  newTransaction;
    }
    
    public void validate(Transaction transaction){
    	walletRepository.findById(transaction.payee())
            .map(payee -> walletRepository.findById(transaction.payer())
            		.map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
            		.orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction))))
            .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
    }

	private boolean isTransactionValid(Transaction transaction, Wallet payer) {
		return payer.type() == WalletType.COMUM.getValue() &&
		    payer.balance().compareTo(transaction.value()) >= 0 &&
		    !payer.id().equals(transaction.payee());
	}
}
