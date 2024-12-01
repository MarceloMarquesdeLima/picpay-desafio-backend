package com.marcelomarques.picpay_desafio_backend.notification;

import org.springframework.stereotype.Service;

import com.marcelomarques.picpay_desafio_backend.transaction.Transaction;

@Service
public class NotificationService {
	private final NotificationProducer notificationProducer;
	
	public NotificationService(NotificationProducer notificationProducer) {
		this.notificationProducer = notificationProducer;
	}
	
	public void notify(Transaction transaction) {
		notificationProducer.sendNotification(transaction);
	}
}
