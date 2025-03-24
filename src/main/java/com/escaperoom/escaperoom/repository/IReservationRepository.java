package com.escaperoom.escaperoom.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.User;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findByUserIdUser(int userId);

	// Vérifie si un utilisateur a déjà réservé un événement
	boolean existsByUserAndEvenement(User user, Evenement evenement);

	// Recherche les réservations existantes d'un événement dont le créneau horaire se chevauche
	boolean existsByEvenementAndHeureDebutLessThanAndHeureFinGreaterThan(Evenement evenement, LocalTime heureFin,
			LocalTime heureDebut);

	// Compter le nombre de réservations pour un utilisateur donné
	long countByUser(User user);

	boolean existsByEvenement(Evenement evenement);

	public Reservation findByEvenement(Evenement evenement);

	public long countByUserAndEvenementAndHeureDebut(User user, Evenement evenement, LocalTime heureDebut);

	boolean existsByEvenementAndHeureDebutAndUserNot(Evenement evenement, LocalTime heureDebut, User user);

}
