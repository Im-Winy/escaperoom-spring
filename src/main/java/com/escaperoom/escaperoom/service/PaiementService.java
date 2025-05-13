package com.escaperoom.escaperoom.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Paiement;
import com.escaperoom.escaperoom.entity.PaymentRequest;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.StatutPaiement;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IPaiementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;

@Service
public class PaiementService {

	@Autowired
	IPaiementRepository paiementRepository;

	@Autowired
	IReservationRepository reservationRepository;

	@Autowired
	EmailService emailService;

	public Paiement processPayment(PaymentRequest request, Long reservationId) {
		// 1. Recherche la réservation par son ID
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new RuntimeException("Réservation introuvable"));

		// 2. Vérifie que le montant reçu correspond exactement au montant attendu
		BigDecimal montantAttendu = reservation.getMontant(); // montant attendu
		BigDecimal montantRecu = BigDecimal.valueOf(request.getAmount()); // conversion du montant reçu

		// Si les montants ne correspondent pas, on arrête le traitement
		if (montantRecu.compareTo(montantAttendu) != 0) {
			throw new IllegalArgumentException(
					"Le montant du paiement ne correspond pas au montant attendu : " + montantAttendu);
		}

		// 3. Simulation d'approbation : le paiement est refusé si le token est "fail"
		boolean approved = !request.getPaymentToken().equalsIgnoreCase("fail");

		// 4. Création et remplissage de l'objet Paiement
		Paiement paiement = new Paiement();
		paiement.setAmount(request.getAmount());
		paiement.setPaymentToken(request.getPaymentToken());
		paiement.setTransactionId(UUID.randomUUID().toString()); // identifiant unique de transaction
		paiement.setStatut(approved ? StatutPaiement.APPROUVE : StatutPaiement.REFUSE); // statut selon approbation
		paiement.setDatePaiement(LocalDateTime.now()); // date actuelle

		// 5. Sauvegarde du paiement en base
		Paiement savedPaiement = paiementRepository.save(paiement);

		// 6. Si le paiement est approuvé, on l'associe à la réservation
		if (approved) {
			reservation.setPaiement(savedPaiement);
			reservationRepository.save(reservation);
		}

		// 7. Récupération des infos nécessaires pour l'email
		Evenement evenement = reservation.getEvenement();
		User utilisateur = reservation.getUser();
		TimeSlot timeSlot = reservation.getTimeSlot();

		// 8. Formatage de la date et de l’heure du créneau
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		String formattedDate = timeSlot.getDate().format(dateFormatter);
		String formattedTime = timeSlot.getStartTime().format(timeFormatter);

		// 9. Envoi de l’e-mail de confirmation à l’utilisateur
		emailService.sendReservationConfirmation(utilisateur.getEmail(), utilisateur.getUsername(), evenement.getNom(),
				formattedDate, formattedTime);

		// 10. Retourne le paiement sauvegardé
		return savedPaiement;
	}

}
