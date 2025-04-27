package com.escaperoom.escaperoom.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

	// Injection des dépendances des repositories nécessaires
	@Autowired
	IReservationRepository reservationRepository;

	@Autowired
	IUserRepository userRepository;

	@Autowired
	ITimeSlotRepository timeSlotRepository;

	@Autowired
	IEvenementRepository evenementRepository;

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
	    Optional<Reservation> existingReservation = reservationRepository.findByTimeSlotAndEvenement(timeSlot, evenement);

	    if (existingReservation.isPresent()) {
	        throw new IllegalStateException("Ce créneau est déjà réservé pour cet événement.");
	    }

	    // Si le créneau n'est pas réservé pour cet événement, on peut procéder à la réservation

	    // Création de la réservation pour l'événement et l'utilisateur
	    Reservation reservation = new Reservation();
	    reservation.setUser(utilisateur);
	    reservation.setEvenement(evenement);
	    reservation.setTimeSlot(timeSlot);
	    reservation.setDateReservation(LocalDateTime.now());

	    // Sauvegarde de la réservation
	    return reservationRepository.save(reservation);
	}


	// Génère les créneaux horaires d'une journée donnée
	public Object generateTimeSlotsForDay(LocalDate date) {
		
	    // Vérifie si des créneaux existent déjà pour ce jour
	    List<TimeSlot> existingSlots = timeSlotRepository.findByDate(date);
	    if (!existingSlots.isEmpty()) {
	        return "déjà réservé"; // Si créneaux existent, on renvoie ce message
	    }

	    LocalTime start = LocalTime.of(10, 0); // Heure d'ouverture
	    LocalTime end = LocalTime.of(22, 0);   // Heure de fermeture
	    Duration duration = Duration.ofMinutes(60); // Durée d'un créneau

	    List<TimeSlot> slots = new ArrayList<>();

	    while (start.plus(duration).isBefore(end.plusSeconds(1))) {
	        TimeSlot slot = new TimeSlot();
	        slot.setDate(date);
	        slot.setStartTime(start);
	        slot.setEndTime(start.plus(duration));

	        slots.add(slot);
	        start = start.plus(duration); // Passage au créneau suivant
	    }

	    return timeSlotRepository.saveAll(slots);
	}

	
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
