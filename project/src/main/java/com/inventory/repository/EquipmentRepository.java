package com.inventory.repository;

import com.inventory.model.Equipment;
import com.inventory.model.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByCategory(String category);
    List<Equipment> findByCurrentStatus(EquipmentStatus status);
}