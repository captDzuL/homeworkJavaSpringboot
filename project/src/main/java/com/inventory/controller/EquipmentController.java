package com.inventory.controller;

import com.inventory.model.Equipment;
import com.inventory.model.EquipmentCondition;
import com.inventory.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        return ResponseEntity.ok(equipmentService.addEquipment(equipment));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Boolean> checkoutEquipment(
            @RequestParam Long equipmentId,
            @RequestParam Long userId,
            @RequestParam LocalDateTime returnDate) {
        return ResponseEntity.ok(equipmentService.checkoutEquipment(equipmentId, userId, returnDate));
    }

    @PostMapping("/return")
    public ResponseEntity<Boolean> returnEquipment(
            @RequestParam Long equipmentId,
            @RequestParam EquipmentCondition condition) {
        return ResponseEntity.ok(equipmentService.returnEquipment(equipmentId, condition));
    }

    @PostMapping("/{equipmentId}/maintenance")
    public ResponseEntity<Boolean> scheduleMaintenanceForEquipment(
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.scheduleMaintenanceForEquipment(equipmentId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipment() {
        return ResponseEntity.ok(equipmentService.getAvailableEquipment());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Equipment>> searchEquipment(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(equipmentService.searchEquipment(category));
    }
}