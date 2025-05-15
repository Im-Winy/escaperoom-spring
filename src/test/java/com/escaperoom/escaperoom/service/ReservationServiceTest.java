package com.escaperoom.escaperoom.service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;
import com.escaperoom.escaperoom.repository.ITimeSlotRepository;
import com.escaperoom.escaperoom.repository.IUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private IReservationRepository reservationRepository;
    private IUserRepository userRepository;
    private ITimeSlotRepository timeSlotRepository;
    private IEvenementRepository evenementRepository;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // On crée des mocks de toutes les dépendances de ReservationService
        reservationRepository = mock(IReservationRepository.class);
        userRepository = mock(IUserRepository.class);
        timeSlotRepository = mock(ITimeSlotRepository.class);
        evenementRepository = mock(IEvenementRepository.class);
        emailService = mock(EmailService.class);

        // On instancie le service en injectant manuellement les mocks
        reservationService = new ReservationService();
        reservationService.reservationRepository = reservationRepository;
        reservationService.userRepository = userRepository;
        reservationService.timeSlotRepository = timeSlotRepository;
        reservationService.evenementRepository = evenementRepository;
        reservationService.emailService = emailService;
    }

    @Test
    void testReserve_Success() {
        // Préparation des IDs
        Long userId = 1L;
        Long eventId = 2L;
        Long timeSlotId = 3L;

        // Création d'un utilisateur fictif
        User user = new User();
        user.setIdUser(userId);
        user.setEmail("test@example.com");

        // Création d'un événement fictif avec un prix de 40
        Evenement event = new Evenement();
        event.setIdEvenement(eventId);
        event.setPrix(40);

        // Création d’un créneau horaire fictif
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIdTimeSlot(timeSlotId);

        // Simulation du comportement des repositories
        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reservationRepository.findByTimeSlotAndEvenement(timeSlot, event)).thenReturn(Optional.empty());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution de la méthode à tester
        Reservation result = reservationService.reserve(timeSlotId, userId, eventId);

        // Vérifications
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvenement());
        assertEquals(timeSlot, result.getTimeSlot());

        // Vérification des montants (HT, TVA, TTC) avec échelle 2 décimales
        assertEquals(new BigDecimal("40.00"), result.getMontantHT().setScale(2));
        assertEquals(new BigDecimal("8.00"), result.getMontantTVA().setScale(2));
        assertEquals(new BigDecimal("48.00"), result.getMontant().setScale(2));
    }

    @Test
    void testReserve_TimeSlotNotFound() {
        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> reservationService.reserve(1L, 2L, 3L));

        assertEquals("Ce créneau n'existe pas.", exception.getMessage());
    }

    @Test
    void testReserve_UserNotFound() {
        TimeSlot timeSlot = new TimeSlot();
        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.reserve(1L, 2L, 3L));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void testReserve_EventNotFound() {
        TimeSlot timeSlot = new TimeSlot();
        User user = new User();

        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(evenementRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.reserve(1L, 2L, 3L));

        assertEquals("Événement introuvable", exception.getMessage());
    }

    @Test
    void testReserve_AlreadyReserved() {
        TimeSlot timeSlot = new TimeSlot();
        User user = new User();
        Evenement event = new Evenement();
        Reservation existing = new Reservation();

        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(evenementRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reservationRepository.findByTimeSlotAndEvenement(timeSlot, event)).thenReturn(Optional.of(existing));

        Exception exception = assertThrows(IllegalStateException.class, () -> reservationService.reserve(1L, 2L, 3L));

        assertEquals("Ce créneau est déjà réservé pour cet événement.", exception.getMessage());
    }

    @Test
    void testGenerateTimeSlotsForDay_AlreadyExists() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        when(timeSlotRepository.findByDate(date)).thenReturn(List.of(new TimeSlot()));

        ResponseEntity<?> response = reservationService.generateTimeSlotsForDay(date);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void testGenerateTimeSlotsForDay_Success() {
        LocalDate date = LocalDate.of(2025, 5, 21);
        when(timeSlotRepository.findByDate(date)).thenReturn(List.of());
        when(timeSlotRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = reservationService.generateTimeSlotsForDay(date);
        assertEquals(200, response.getStatusCodeValue());

        List<TimeSlot> slots = (List<TimeSlot>) response.getBody();
        assertNotNull(slots);
        assertEquals(12, slots.size()); // 12 créneaux de 1h entre 10h et 22h
    }

    @Test
    void testGetAvailableTimeSlotsForEvenement_EventNotFound() {
        when(evenementRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> reservationService.getAvailableTimeSlotsForEvenement(1L, LocalDate.now()));

        assertEquals("Événement introuvable", exception.getMessage());
    }
}
