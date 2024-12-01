package com.marcelomarques.picpay_desafio_backend.authorization;

public class UnauthorizedTransactionException extends RuntimeException {
	public UnauthorizedTransactionException(String message) {
		super(message);
	}
}
