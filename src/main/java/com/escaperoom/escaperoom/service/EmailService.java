package com.escaperoom.escaperoom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Async
	public void sendEmail(String toEmail, String subject, String message) {

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(toEmail);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);
		mailMessage.setFrom("williamnyacka14@gmail.com");

		javaMailSender.send(mailMessage);
	}

	public void sendConfirmRegister(String email, String username, String password) {

		String subject = "Confirme ton inscription";
		String message = "Salut " + username + ",\n\nVoici ton mot de passe : " + password;

		sendEmail(email, subject, message);

	}

	public void sendReservationConfirmation(String email, String username, String eventName, String eventDate,
			String eventTime) {

		String subject = "Confirmation de ta réservation";
		String message = "Bonjour " + username + ",\n\n" + "Ta réservation pour l'événement \"" + eventName
				+ "\" a bien été confirmée.\n" + "Voici les détails :\n\n" + "📍 Événement : " + eventName + "\n"
				+ "📅 Date : " + eventDate + "\n" + "🕒 Heure : " + eventTime + "\n\n"
				+ "Nous sommes ravis de te compter parmi nous.\n"
				+ "N'hésite pas à nous contacter si tu as la moindre question.\n\n" + "À très bientôt,\n"
				+ "L'équipe EscapeRoom";

		sendEmail(email, subject, message);
	}

}