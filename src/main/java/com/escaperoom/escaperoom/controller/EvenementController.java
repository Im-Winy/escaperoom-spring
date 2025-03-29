package com.escaperoom.escaperoom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.Difficulte;
import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.service.EvenementService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class EvenementController {

	@Autowired
	EvenementService evenementService;

	// Enregistre un évènement
	@PostMapping("/evenement")
	public Evenement createEvenement(@Validated @RequestBody(required = false) Evenement evenement) {
		return evenementService.saveEvenement(evenement);
	}

	// Récupère tout les évènements
	@GetMapping("/evenement/list")
	public List<Evenement> getAllEvenements(@Validated @RequestBody(required = false) Evenement evenement) {
		return evenementService.getEvenements();
	}
	
	// Récupère un évènement
	@GetMapping("/evenement/{id}")
	public Evenement getEvenementById(@PathVariable("id") Long idEvenement){
		return evenementService.getEvenementById(idEvenement);
	}
	
	// Met à jour un évènement
	@PutMapping("/update/evenement/{id}")
    public ResponseEntity<Evenement> updateEvenement(
            @PathVariable("id") long idEvenement,
            @Validated @RequestBody(required = false) Evenement evenement
    		){
        Evenement updatedEvenement =  evenementService.updateEvenement(idEvenement, evenement); 
        return new ResponseEntity<>(updatedEvenement, HttpStatus.OK);
    }
}
