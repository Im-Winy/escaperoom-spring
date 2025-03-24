package com.escaperoom.escaperoom.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.repository.IEvenementRepository;

@Service
public class EvenementService {

	@Autowired
	IEvenementRepository evenementRepository;

	// Enregistre un évènement
	public Evenement saveEvenement(Evenement evenement) {
		return evenementRepository.save(evenement);
	}

	// Récupère un seul évènement par son id
	public Evenement getEvenementById(Long idEvenement) {
		return evenementRepository.findById(idEvenement).get();
	}

	// Récupère tout les évènements
	public List<Evenement> getEvenements() {
		return evenementRepository.findAll();
	}

	// Supprime un évènement
	public void deleteUser(Evenement evenement) {
		evenementRepository.delete(evenement);
	}

	// Met à jour un évènement
	public Evenement updateEvenement(Long idEvenement, Evenement evenement) {
		Evenement event = evenementRepository.findById(idEvenement).get();
		event.setNom(event.getNom());
		event.setDescription(event.getDescription());
		event.setNom(event.getNom());
		event.setImage(event.getImage());
		event.setDuree(event.getDuree());
		event.setNbeJoueurMax(event.getNbeJoueurMax());
		event.setPrix(event.getPrix());
		event.setDifficulte(event.getDifficulte());
		return evenementRepository.save(event);
	}

	// Show one
	public Optional<Evenement> findById(Long id) {
		return evenementRepository.findById(id);
	}
}
