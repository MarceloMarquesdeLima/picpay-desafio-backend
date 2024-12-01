package com.marcelomarques.picpay_desafio_backend.notification;

import org.springframework.stereotype.Service;

import com.marcelomarques.picpay_desafio_backend.transaction.Transaction;

import org.springframework.kafka.core.KafkaTemplate;

@Service
public class NotificationProducer {
	private final KafkaTemplate<String, Transaction> kafkaTemplate;
	public NotificationProducer(KafkaTemplate<String, Transaction>kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void sendNotification(Transaction transaction) {
		kafkaTemplate.send("transaction-notifiction", transaction);
	}
}
