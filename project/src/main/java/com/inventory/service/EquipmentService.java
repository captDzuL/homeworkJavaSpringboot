package com.inventory.service;

import com.inventory.model.*;
import com.inventory.repository.EquipmentRepository;
import com.inventory.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public Equipment addEquipment(Equipment equipment) {
        equipment.setCurrentStatus(EquipmentStatus.AVAILABLE);
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public boolean checkoutEquipment(Long equipmentId, Long userId, LocalDateTime returnDate) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
            .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (equipment.getCurrentStatus() != EquipmentStatus.AVAILABLE) {
            throw new RuntimeException("Equipment not available for checkout");
        }

        equipment.setCurrentStatus(EquipmentStatus.CHECKED_OUT);
        equipmentRepository.save(equipment);

        Rental rental = new Rental();
        rental.setEquipment(equipment);
        rental.setUser(new User()); // TODO: Get user from UserService
        rental.setCheckoutDate(LocalDateTime.now());
        rental.setExpectedReturnDate(returnDate);
        rental.setStatus(RentalStatus.ACTIVE);
        
        rentalRepository.save(rental);
        return true;
    }

    @Transactional
    public boolean returnEquipment(Long equipmentId, EquipmentCondition condition) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
            .orElseThrow(() -> new RuntimeException("Equipment not found"));

        Rental rental = rentalRepository.findByEquipmentIdAndStatus(equipmentId, RentalStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("No active rental found"));

        rental.setStatus(condition == EquipmentCondition.NEEDS_MAINTENANCE ? 
            RentalStatus.DAMAGED : RentalStatus.RETURNED);
        rental.setActualReturnDate(LocalDateTime.now());
        rentalRepository.save(rental);

        equipment.setCondition(condition);
        equipment.setCurrentStatus(condition == EquipmentCondition.NEEDS_MAINTENANCE ? 
            EquipmentStatus.MAINTENANCE : EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);

        return true;
    }

    @Transactional
    public boolean scheduleMaintenanceForEquipment(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
            .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (equipment.getCurrentStatus() == EquipmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Equipment is currently checked out");
        }

        equipment.setCurrentStatus(EquipmentStatus.MAINTENANCE);
        equipment.setLastMaintenanceDate(LocalDateTime.now());
        equipmentRepository.save(equipment);
        return true;
    }

    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findByCurrentStatus(EquipmentStatus.AVAILABLE);
    }

    public List<Equipment> searchEquipment(String category) {
        return category != null ? 
            equipmentRepository.findByCategory(category) : 
            equipmentRepository.findAll();
    }
}