package com.inventory.service;

import com.inventory.model.*;
import com.inventory.repository.EquipmentRepository;
import com.inventory.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private Equipment testEquipment;
    private Rental testRental;

    @BeforeEach
    void setUp() {
        testEquipment = new Equipment();
        testEquipment.setId(1L);
        testEquipment.setName("Camera");
        testEquipment.setCategory("Video");
        testEquipment.setSerialNumber("CAM001");
        testEquipment.setCurrentStatus(EquipmentStatus.AVAILABLE);
        testEquipment.setCondition(EquipmentCondition.EXCELLENT);

        testRental = new Rental();
        testRental.setId(1L);
        testRental.setEquipment(testEquipment);
        testRental.setStatus(RentalStatus.ACTIVE);
    }

    @Test
    void addEquipment_ShouldSaveAndReturnEquipment() {
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(testEquipment);

        Equipment result = equipmentService.addEquipment(testEquipment);

        assertNotNull(result);
        assertEquals(EquipmentStatus.AVAILABLE, result.getCurrentStatus());
        verify(equipmentRepository).save(testEquipment);
    }

    @Test
    void checkoutEquipment_WhenEquipmentAvailable_ShouldSucceed() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(testEquipment);
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);

        boolean result = equipmentService.checkoutEquipment(1L, 1L, LocalDateTime.now().plusDays(7));

        assertTrue(result);
        assertEquals(EquipmentStatus.CHECKED_OUT, testEquipment.getCurrentStatus());
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void checkoutEquipment_WhenEquipmentNotAvailable_ShouldThrowException() {
        testEquipment.setCurrentStatus(EquipmentStatus.CHECKED_OUT);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));

        assertThrows(RuntimeException.class, () -> 
            equipmentService.checkoutEquipment(1L, 1L, LocalDateTime.now().plusDays(7))
        );
    }

    @Test
    void returnEquipment_WhenValidReturn_ShouldSucceed() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(rentalRepository.findByEquipmentIdAndStatus(1L, RentalStatus.ACTIVE))
            .thenReturn(Optional.of(testRental));

        boolean result = equipmentService.returnEquipment(1L, EquipmentCondition.GOOD);

        assertTrue(result);
        assertEquals(EquipmentStatus.AVAILABLE, testEquipment.getCurrentStatus());
        assertEquals(RentalStatus.RETURNED, testRental.getStatus());
        verify(equipmentRepository).save(testEquipment);
        verify(rentalRepository).save(testRental);
    }

    @Test
    void getAvailableEquipment_ShouldReturnAvailableEquipment() {
        List<Equipment> availableEquipment = Arrays.asList(testEquipment);
        when(equipmentRepository.findByCurrentStatus(EquipmentStatus.AVAILABLE))
            .thenReturn(availableEquipment);

        List<Equipment> result = equipmentService.getAvailableEquipment();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(equipmentRepository).findByCurrentStatus(EquipmentStatus.AVAILABLE);
    }

    @Test
    void searchEquipment_WithCategory_ShouldReturnFilteredEquipment() {
        List<Equipment> filteredEquipment = Arrays.asList(testEquipment);
        when(equipmentRepository.findByCategory("Video")).thenReturn(filteredEquipment);

        List<Equipment> result = equipmentService.searchEquipment("Video");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(equipmentRepository).findByCategory("Video");
    }
}