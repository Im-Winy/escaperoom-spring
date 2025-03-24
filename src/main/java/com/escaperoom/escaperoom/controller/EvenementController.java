package com.escaperoom.escaperoom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.service.EvenementService;

@RestController
public class EvenementController {

	@Autowired
	EvenementService evenementService;
	
	//Enregistre un évènement
	@PostMapping("/evenements")
	public Evenement createEvenement(@Validated @RequestBody(required = false) Evenement evenement) {
		return evenementService.saveEvenement(evenement);	
	}
	
	//Récupère tout les évènements
	@GetMapping("/evenements")
	public List<Evenement> getAllEvenements(@Validated @RequestBody(required = false) Evenement evenement){
		return evenementService.getEvenements();	
	}
}
