package com.escaperoom.escaperoom.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.Statut;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;
import com.escaperoom.escaperoom.repository.IUserRepository;

@Service
public class ReservationService {

	@Autowired
	IReservationRepository reservationRepository;

	@Autowired
	IUserRepository userRepository;

	@Autowired
	IEvenementRepository evenementRepository;

	// Enregistrer une réservation
	public String reserverEvenement(Long idUser, Long idEvenement, LocalTime heureDebut, Duration duree) {

		// 1°) Vérification de la validité de l'heure de début et de la durée
		if (heureDebut == null || duree == null || duree.isNegative() || duree.isZero()) {
			return "Heure de début ou durée invalide";
		}

		// 2°) Recherche de l'utilisateur et de l'événement
		User utilisateur = userRepository.findById(idUser)
				.orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

		Evenement evenement = evenementRepository.findById(idEvenement)
				.orElseThrow(() -> new RuntimeException("Événement introuvable"));

		// 3°) Vérifier si un autre utilisateur a déjà réservé cet événement au même
		// horaire
		boolean autreUtilisateurReserve = reservationRepository.existsByEvenementAndHeureDebutAndUserNot(evenement,
				heureDebut, utilisateur);

		if (autreUtilisateurReserve) {
			return "Un autre utilisateur a déjà réservé cet événement à cet horaire.";
		}

		// 4°) Vérifier le nombre de réservations de cet utilisateur pour cet événement
		// et cette heure
		long count = reservationRepository.countByUserAndEvenementAndHeureDebut(utilisateur, evenement, heureDebut);

		if (count >= 3) {
			return "L'utilisateur ne peut pas réserver plus de 3 fois cet événement à cette heure.";
		}

		// 5°) Calcul de l'heure de fin de la réservation
		LocalTime heureFin = heureDebut.plus(duree);

		// 6°) Création de la nouvelle réservation
		Reservation reserv = new Reservation();
		reserv.setUser(utilisateur);
		reserv.setEvenement(evenement);
		reserv.setHeureDebut(heureDebut);
		reserv.setHeureFin(heureFin);
		reserv.setDuree(duree);
		reserv.setStatut(Statut.réservé);
		reserv.setDateReservation(LocalDateTime.now());

		// 7°) Sauvegarde de la réservation dans la base de données
		reservationRepository.save(reserv);

		// 8°) Retour de la réponse de succès
		return "Réservation réussie ! " + "Événement : " + evenement.getNom() + ", " + "Utilisateur : " + idUser + ", "
				+ "Heure de début : " + heureDebut + ", " + "Durée : " + duree.toMinutes() + " minutes.";
	}

	// Mise à jour de la réservation
	public String mettreAJourReservation(Long idReservation, LocalTime heureDebut, Duration duree) {
		// 1°) Vérification de la validité de l'heure de début et de la durée
		if (heureDebut == null || duree == null || duree.isNegative() || duree.isZero()) {
			return "Heure de début ou durée invalide";
		}

		// 2°) Recherche de la réservation existante
		Reservation reservation = reservationRepository.findById(idReservation)
				.orElseThrow(() -> new RuntimeException("Réservation introuvable"));

		// 3°) Vérifier si un autre utilisateur a déjà réservé cet événement au même
		// horaire
		boolean autreUtilisateurReserve = reservationRepository.existsByEvenementAndHeureDebutAndUserNot(
				reservation.getEvenement(), heureDebut, reservation.getUser());

		if (autreUtilisateurReserve) {
			return "Un autre utilisateur a déjà réservé cet événement à cet horaire.";
		}

		// 4°) Vérifier le nombre de réservations de cet utilisateur pour cet événement et cette heure
		long count = reservationRepository.countByUserAndEvenementAndHeureDebut(reservation.getUser(),
				reservation.getEvenement(), heureDebut);

		if (count >= 3) {
			return "L'utilisateur ne peut pas réserver plus de 3 fois cet événement à cette heure.";
		}

		// 5°) Calcul de la nouvelle heure de fin de la réservation
		LocalTime nouvelleHeureFin = heureDebut.plus(duree);

		// 6°) Mise à jour des informations de la réservation
		reservation.setHeureDebut(heureDebut);
		reservation.setHeureFin(nouvelleHeureFin);
		reservation.setDuree(duree);
		reservation.setDateReservation(LocalDateTime.now());

		// 7°) Sauvegarde de la réservation mise à jour dans la base de données
		reservationRepository.save(reservation);

		// 8°) Retour de la réponse de succès
		return "Mise à jour réussie ! " + "Événement : " + reservation.getEvenement().getNom() + ", " + "Utilisateur : "
				+ reservation.getUser().getNom() + ", " + "Nouvelle heure de début : " + heureDebut + ", "
				+ "Nouvelle durée : " + duree.toMinutes() + " minutes.";
	}

	// Récupère une seule réservation par son id
	public Reservation getReservationById(Long idReservation) {
		return reservationRepository.findById(idReservation).get();
	}

	// Récupère toutes les réservations d'un utilisateur en fonction de son id
	public List<Reservation> getReservationsByUserId(int userId) {
		return reservationRepository.findByUserIdUser(userId);
	}

	// Récupère toute les réservations
	public List<Reservation> getReservation() {
		return reservationRepository.findAll();
	}

	// Supprime une réservation
	public void deleteReservation(Reservation reservation) {
		reservationRepository.delete(reservation);
	}

}
