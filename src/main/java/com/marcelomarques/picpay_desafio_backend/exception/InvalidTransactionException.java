package com.marcelomarques.picpay_desafio_backend.exception;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
      super(message);
    }
  
  }
