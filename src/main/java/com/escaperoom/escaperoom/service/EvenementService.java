package com.escaperoom.escaperoom.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Difficulte;
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
	public void deleteEvenement(Evenement evenement) {
		evenementRepository.delete(evenement);
	}

	// Met à jour un évènement
	public Evenement updateEvenement(Long idEvenement, String nom, String description, String image, int duree, int nbeJoueurMax, int prix, String difficulte) {
		Evenement event = evenementRepository.findById(idEvenement).get();
		event.setNom(nom);
		event.setDescription(description);
		event.setImage(image);
		event.setDuree(duree);
		event.setNbeJoueurMax(nbeJoueurMax);
		event.setPrix(prix);
		event.setDifficulte(Difficulte.valueOf(difficulte.toUpperCase()));
		 
		return evenementRepository.save(event);
	}

	// Show one
	public Optional<Evenement> findById(Long id) {
		return evenementRepository.findById(id);
	}

	// Supprime un évènement par son identifiant
	public void deleteEvenement(Long id) {
		evenementRepository.deleteById(id);
	}
}
