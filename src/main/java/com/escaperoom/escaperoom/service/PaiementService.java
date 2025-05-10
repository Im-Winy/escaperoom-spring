package com.escaperoom.escaperoom.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Paiement;
import com.escaperoom.escaperoom.entity.PaymentRequest;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.StatutPaiement;
import com.escaperoom.escaperoom.repository.IPaiementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;

@Service
public class PaiementService {

	@Autowired
	IPaiementRepository paiementRepository;
	
	@Autowired
	IReservationRepository reservationRepository;

	public Paiement processPayment(PaymentRequest request, Long reservationId) {
	    boolean approved = !request.getPaymentToken().equalsIgnoreCase("fail");

	    // Créer un paiement
	    Paiement paiement = new Paiement();
	    paiement.setAmount(request.getAmount());
	    paiement.setPaymentToken(request.getPaymentToken());
	    paiement.setTransactionId(UUID.randomUUID().toString());
	    paiement.setStatut(approved ? StatutPaiement.APPROUVE : StatutPaiement.REFUSE);
	    paiement.setDatePaiement(LocalDateTime.now());

	    Paiement savedPaiement = paiementRepository.save(paiement);

	    if (approved) {
	        // Si le paiement est approuvé, lier le paiement à la réservation
	        Reservation reservation = reservationRepository.findById(reservationId)
	                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

	        reservation.setPaiement(savedPaiement);
	        reservationRepository.save(reservation);
	    }

	    return savedPaiement;
	}

}
