package com.escaperoom.escaperoom.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
	
	@ManyToOne
	@JsonBackReference(value = "timeslot-reservation")
    @JoinColumn(name = "ID_CRENEAU_HORAIRE")
    private TimeSlot timeSlot;
	
	@ManyToOne
	@JsonBackReference(value = "user-reservation")
	@JoinColumn(name="ID_USER")
	private User user;
	
	@ManyToOne
	@JsonBackReference(value = "evenement-reservation")
	@JoinColumn(name="ID_EVENEMENT")
	private Evenement evenement;
	
	@OneToOne
	@JsonBackReference(value = "paiement-reservation")
	@JoinColumn(name="ID_PAIEMENT")
	private Paiement paiement;

	public Reservation() {
		super();
	}

	public Reservation(Long idReservation, LocalDateTime dateReservation, User user, Evenement evenement, TimeSlot timeSlot, Paiement paiement) {
		super();
		this.idReservation = idReservation;
		this.dateReservation = dateReservation;
		this.user = user;
		this.evenement = evenement;
		this.timeSlot = timeSlot;
		this.paiement = paiement;
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

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Paiement getPaiement() {
		return paiement;
	}

	public void setPaiement(Paiement paiement) {
		this.paiement = paiement;
	}	
	
}
