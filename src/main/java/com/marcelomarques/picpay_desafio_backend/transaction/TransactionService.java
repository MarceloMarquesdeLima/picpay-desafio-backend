package com.marcelomarques.picpay_desafio_backend.transaction;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcelomarques.picpay_desafio_backend.authorization.AuthorizerService;
import com.marcelomarques.picpay_desafio_backend.notification.NotificationService;
import com.marcelomarques.picpay_desafio_backend.wallet.Wallet;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletRepository;
import com.marcelomarques.picpay_desafio_backend.wallet.WalletType;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;
    private Transaction save;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }
    
    @Transactional
    public Transaction create(Transaction transaction) {

        validate(transaction);

        var newTransaction = transactionRepository.save(transaction);

        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();

        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));

        authorizerService.authorize(transaction);
        
        notificationService.notify(transaction);

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

	public List<Transaction> list() {
		return transactionRepository.findAll();
	}
}
