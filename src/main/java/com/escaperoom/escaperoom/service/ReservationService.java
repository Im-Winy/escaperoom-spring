package com.escaperoom.escaperoom.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;
import com.escaperoom.escaperoom.repository.ITimeSlotRepository;
import com.escaperoom.escaperoom.repository.IUserRepository;

@Service
public class ReservationService {

	// Injection des dépendances
	@Autowired
	IReservationRepository reservationRepository;

	@Autowired
	IUserRepository userRepository;

	@Autowired
	ITimeSlotRepository timeSlotRepository;

	@Autowired
	IEvenementRepository evenementRepository;

	@Autowired
	EmailService emailService;

	public Reservation reserve(Long timeSlotId, Long idUser, Long idEvenement) {

		// Vérifie si le créneau existe
		TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
				.orElseThrow(() -> new IllegalStateException("Ce créneau n'existe pas."));

		// Récupération de l'utilisateur
		User utilisateur = userRepository.findById(idUser)
				.orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

		// Récupération de l'événement
		Evenement evenement = evenementRepository.findById(idEvenement)
				.orElseThrow(() -> new RuntimeException("Événement introuvable"));

		// Vérifie si le créneau est déjà réservé pour cet événement
		Optional<Reservation> existingReservation = reservationRepository.findByTimeSlotAndEvenement(timeSlot,
				evenement);

		if (existingReservation.isPresent()) {
			throw new IllegalStateException("Ce créneau est déjà réservé pour cet événement.");
		}

		// Si le créneau n'est pas réservé pour cet événement, on peut procéder à la
		// réservation

		// Création de la réservation pour l'événement et l'utilisateur
		Reservation reservation = new Reservation();
		reservation.setUser(utilisateur);
		reservation.setEvenement(evenement);
		reservation.setTimeSlot(timeSlot);
		reservation.setDateReservation(LocalDateTime.now());

		// Calcul des montants
		int prixInt = evenement.getPrix();
		BigDecimal montantHT = BigDecimal.valueOf(prixInt); // conversion de int en BigDecimal
		BigDecimal tauxTVA = new BigDecimal("0.20"); // 20 % de TVA
		BigDecimal montantTVA = montantHT.multiply(tauxTVA);
		BigDecimal montantTTC = montantHT.add(montantTVA);

		reservation.setMontantHT(montantHT);
		reservation.setMontantTVA(montantTVA);
		reservation.setMontant(montantTTC);
		reservation.setPaiement(null); // Pas encore de paiement

		// Sauvegarde de la réservation
		Reservation savedReservation = reservationRepository.save(reservation);

		// Formatage de la date et de l'heure
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		String formattedDate = timeSlot.getDate().format(dateFormatter);
		String formattedTime = timeSlot.getStartTime().format(timeFormatter);

		// Envoi de l'e-mail de confirmation
		emailService.sendReservationConfirmation(utilisateur.getEmail(), utilisateur.getUsername(), evenement.getNom(),
				formattedDate, // Date formatée
				formattedTime // Heure formatée
		);

		// Retour de la réservation
		return savedReservation;
	}

	// Génère les créneaux horaires d'une journée donnée
	public ResponseEntity<?> generateTimeSlotsForDay(LocalDate date) {

		// Vérifie si des créneaux existent déjà pour ce jour
		List<TimeSlot> existingSlots = timeSlotRepository.findByDate(date);
		if (!existingSlots.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Les créneaux pour cette journée existent déjà.");
		}

		LocalTime start = LocalTime.of(10, 0); // Heure d'ouverture
		LocalTime end = LocalTime.of(22, 0); // Heure de fermeture
		Duration duration = Duration.ofMinutes(60); // Durée d'un créneau

		List<TimeSlot> slots = new ArrayList<>();

		// Tant que l’heure de début plus la durée du créneau n’excède pas l’heure de
		// fermeture, on continue la génération.
		while (!start.plus(duration).isAfter(end)) {
			TimeSlot slot = new TimeSlot();
			slot.setDate(date);
			slot.setStartTime(start);
			slot.setEndTime(start.plus(duration));

			slots.add(slot);
			start = start.plus(duration); // Passage au créneau suivant
		}

		List<TimeSlot> savedSlots = timeSlotRepository.saveAll(slots);
		return ResponseEntity.ok(savedSlots);
	}

	// Retourne la liste des créneaux horaires disponibles
	public List<TimeSlot> getTimeSlotsNonReservesPourEvenement(Long evenementId, LocalDate selectedDate) {
		Evenement evenement = evenementRepository.findById(evenementId)
				.orElseThrow(() -> new RuntimeException("Événement introuvable"));
		return reservationRepository.findTimeSlotsNotReservedForEvenementAndDate(evenement, selectedDate);
	}

	// Récupère une réservation par son identifiant
	public Reservation getReservationById(Long idReservation) {
		return reservationRepository.findById(idReservation).get();
	}

	// Récupère toutes les réservations effectuées par un utilisateur donné
	public List<Reservation> getReservationsByUserId(int userId) {
		return reservationRepository.findByUserIdUser(userId);
	}

	// Récupère toutes les réservations existantes
	public List<Reservation> getReservation() {
		return reservationRepository.findAll();
	}

	// Supprime une réservation
	public void deleteReservation(Reservation reservation) {
		reservationRepository.delete(reservation);
	}
}
