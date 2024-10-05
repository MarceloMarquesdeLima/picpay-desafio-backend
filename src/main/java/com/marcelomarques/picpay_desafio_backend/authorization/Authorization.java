package com.marcelomarques.picpay_desafio_backend.authorization;

public record  Authorization (String message) {
    public boolean isAutorization(){
        return  message.equals("Autorizado");
    }
}
