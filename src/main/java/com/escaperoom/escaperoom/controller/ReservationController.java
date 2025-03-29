package com.escaperoom.escaperoom.controller;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.service.ReservationService;

@RestController
@RequestMapping("/api/auth/")
@CrossOrigin("*")
public class ReservationController {

	@Autowired
	ReservationService reservationService;
	
	//Enregistre une réservation
	@PostMapping("/reserver/{idUser}/{idEvenement}")
    public ResponseEntity<String> reserverEvenement(
    		@PathVariable Long idUser,
    		@PathVariable Long idEvenement,
    		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
    		@RequestParam Duration duree) {

        String message = reservationService.reserverEvenement(idUser, idEvenement, heureDebut, duree);

        if (message.contains("Réservation réussie !")) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
	
	//Met à jour une réservation
	@PutMapping("/mis-a-jour/{idReservation}")
    public ResponseEntity<String> mettreAJourReservation(
            @PathVariable Long idReservation,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam Duration duree) {
        
        String resultat = reservationService.mettreAJourReservation(idReservation, heureDebut, duree);
        
        if (resultat.startsWith("Mise à jour réussie")) {
            return ResponseEntity.ok(resultat);
        } else {
            return ResponseEntity.badRequest().body(resultat);
        }
    }
	
	//Récupère toutes les réservations
	@GetMapping("/reservations")
	public List<Reservation> getAllUsers(@Validated @RequestBody(required = false) User user){
		return reservationService.getReservation();	
	}
	
	//Récupère toutes les réservations d'un utilisateur
	@GetMapping("reservations/utilisateurs/{idUser}")
    public ResponseEntity<List<Reservation>> getReservationsByUser(@PathVariable int idUser) {
        List<Reservation> reservation = reservationService.getReservationsByUserId(idUser);
        if (reservation == null || reservation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }
    }
    
    //Supprime une réservation
    @DeleteMapping("/reservations/{id}")
	public ResponseEntity<Reservation> deleteUser(@PathVariable(name="id") Long idReservation){			
		Reservation reservation = reservationService.getReservationById(idReservation);
		if(reservation == null) {
			return ResponseEntity.notFound().build();			
		}
		reservationService.deleteReservation(reservation);
		return ResponseEntity.ok().body(reservation);
	}
	
}
