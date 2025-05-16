package com.escaperoom.escaperoom.service;

import com.escaperoom.escaperoom.entity.Difficulte;
import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EvenementServiceTest {

    private EvenementService evenementService;
    private IEvenementRepository evenementRepository;

    @BeforeEach
    void setUp() {
        evenementRepository = mock(IEvenementRepository.class);
        evenementService = new EvenementService();
        evenementService.evenementRepository = evenementRepository;
    }

    @Test
    void testSaveEvenement() {
        Evenement evenement = new Evenement();
        when(evenementRepository.save(evenement)).thenReturn(evenement);

        Evenement saved = evenementService.saveEvenement(evenement);

        assertEquals(evenement, saved);
        verify(evenementRepository, times(1)).save(evenement);
    }

    @Test
void testSaveEvenement_WithParams() {
    // Arrange
    when(evenementRepository.save(any(Evenement.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Evenement saved = evenementService.saveEvenement("Nom", "Desc", "image.png", 60, 5, 100, "normal");

    // Assert
    assertEquals("Nom", saved.getNom());
    assertEquals("Desc", saved.getDescription());
    assertEquals("image.png", saved.getImage());
    assertEquals(60, saved.getDuree());
    assertEquals(5, saved.getNbeJoueurMax());
    assertEquals(100, saved.getPrix());
    assertEquals(Difficulte.NORMAL, saved.getDifficulte());
    verify(evenementRepository).save(any(Evenement.class));
}


    @Test
    void testGetEvenementById() {
        Evenement evenement = new Evenement();
        evenement.setIdEvenement(1L);
        when(evenementRepository.findById(1L)).thenReturn(Optional.of(evenement));

        Evenement result = evenementService.getEvenementById(1L);

        assertEquals(1L, result.getIdEvenement());
        verify(evenementRepository).findById(1L);
    }

    @Test
    void testGetEvenements() {
        List<Evenement> list = Arrays.asList(new Evenement(), new Evenement());
        when(evenementRepository.findAll()).thenReturn(list);

        List<Evenement> result = evenementService.getEvenements();

        assertEquals(2, result.size());
    }

    @Test
    void testDeleteEvenement() {
        Evenement evenement = new Evenement();
        evenementService.deleteEvenement(evenement);

        verify(evenementRepository, times(1)).delete(evenement);
    }

    @Test
    void testDeleteEvenementById() {
        evenementService.deleteEvenement(10L);
        verify(evenementRepository, times(1)).deleteById(10L);
    }

    @Test
    void testUpdateEvenement() {
        // Arrange
        Evenement evenement = new Evenement();
        evenement.setIdEvenement(1L);
        evenement.setNom("Old");
        evenement.setDescription("OldDesc");
        evenement.setDifficulte(Difficulte.NORMAL);

        when(evenementRepository.findById(1L)).thenReturn(Optional.of(evenement));
        when(evenementRepository.save(any(Evenement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Evenement updated = evenementService.updateEvenement(1L, "New", "Desc", "img.png", 90, 6, 120, "intermediaire");

        // Assert
        assertEquals("New", updated.getNom());
        assertEquals("Desc", updated.getDescription());
        assertEquals("img.png", updated.getImage());
        assertEquals(90, updated.getDuree());
        assertEquals(6, updated.getNbeJoueurMax());
        assertEquals(120, updated.getPrix());
        assertEquals(Difficulte.INTERMEDIAIRE, updated.getDifficulte()); // Vérifie que cette valeur existe bien dans ta
                                                                         // classe enum

        verify(evenementRepository).save(evenement);
    }

    @Test
    void testFindById() {
        Evenement evenement = new Evenement();
        when(evenementRepository.findById(2L)).thenReturn(Optional.of(evenement));

        Optional<Evenement> result = evenementService.findById(2L);

        assertTrue(result.isPresent());
        verify(evenementRepository).findById(2L);
    }
}
