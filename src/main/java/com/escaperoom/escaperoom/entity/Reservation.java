package com.escaperoom.escaperoom.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="RESERVATIONS")
public class Reservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_RESERVATION")
	private Long idReservation;
	
	@Column(name="DATE_RESERVATION")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime dateReservation;
	
	@Column(name="HEURE_DEBUT")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime heureDebut;
	
	@Column(name="HEURE_FIN")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime heureFin;
	
	@Column(name="DUREE")
	private Duration duree;
	
	@Column(name="STATUT")
	@Enumerated(EnumType.STRING)
	private Statut statut;
	
	@ManyToOne
	@JsonBackReference(value = "user-reservation")
	@JoinColumn(name="ID_USER")
	private User user;
	
	@ManyToOne
	@JsonBackReference(value = "evenement-reservation")
	@JoinColumn(name="ID_EVENEMENT")
	private Evenement evenement;

	public Reservation() {
		super();
	}

	public Reservation(Long idReservation, LocalDateTime dateReservation, LocalTime heureDebut, LocalTime heureFin, Duration duree,
			User user, Evenement evenement) {
		super();
		this.idReservation = idReservation;
		this.dateReservation = dateReservation;
		this.heureDebut = heureDebut;
		this.heureFin = heureDebut;
		this.duree = duree;
		this.user = user;
		this.evenement = evenement;
	}

	public Long getIdReservation() {
		return idReservation;
	}

	public void setIdReservation(Long idReservation) {
		this.idReservation = idReservation;
	}

	public LocalDateTime getDateReservation() {
		return dateReservation;
	}

	public void setDateReservation(LocalDateTime dateReservation) {
		this.dateReservation = dateReservation;
	}

	public LocalTime getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(LocalTime heureDebut) {
		this.heureDebut = heureDebut;
	}

	public LocalTime getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(LocalTime heureFin) {
		this.heureFin = heureFin;
	}

	public Duration getDuree() {
		return duree;
	}

	public void setDuree(Duration duree) {
		this.duree = duree;
	}

	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Evenement getEvenement() {
		return evenement;
	}

	public void setEvenement(Evenement evenement) {
		this.evenement = evenement;
	}
	
}
