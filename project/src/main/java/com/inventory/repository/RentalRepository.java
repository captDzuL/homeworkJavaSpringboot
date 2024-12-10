package com.inventory.repository;

import com.inventory.model.Rental;
import com.inventory.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByEquipmentIdAndStatus(Long equipmentId, RentalStatus status);
    List<Rental> findByUserId(Long userId);
    
    @Query("SELECT r FROM Rental r WHERE r.status = 'ACTIVE' AND r.expectedReturnDate < :now")
    List<Rental> findOverdueRentals(LocalDateTime now);
}