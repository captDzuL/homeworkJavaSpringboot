package com.inventory.controller;

import com.inventory.model.Equipment;
import com.inventory.model.EquipmentCondition;
import com.inventory.model.EquipmentStatus;
import com.inventory.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipmentController.class)
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    private Equipment testEquipment;

    @BeforeEach
    void setUp() {
        testEquipment = new Equipment();
        testEquipment.setId(1L);
        testEquipment.setName("Camera");
        testEquipment.setCategory("Video");
        testEquipment.setSerialNumber("CAM001");
        testEquipment.setCurrentStatus(EquipmentStatus.AVAILABLE);
        testEquipment.setCondition(EquipmentCondition.EXCELLENT);
    }

    @Test
    void addEquipment_ShouldReturnCreatedEquipment() throws Exception {
        when(equipmentService.addEquipment(any(Equipment.class))).thenReturn(testEquipment);

        mockMvc.perform(post("/api/equipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Camera\",\"category\":\"Video\",\"serialNumber\":\"CAM001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Camera"))
                .andExpect(jsonPath("$.category").value("Video"));
    }

    @Test
    void checkoutEquipment_ShouldReturnSuccess() throws Exception {
        when(equipmentService.checkoutEquipment(any(Long.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(true);

        mockMvc.perform(post("/api/equipment/checkout")
                .param("equipmentId", "1")
                .param("userId", "1")
                .param("returnDate", LocalDateTime.now().plusDays(7).toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void returnEquipment_ShouldReturnSuccess() throws Exception {
        when(equipmentService.returnEquipment(any(Long.class), any(EquipmentCondition.class)))
                .thenReturn(true);

        mockMvc.perform(post("/api/equipment/return")
                .param("equipmentId", "1")
                .param("condition", "GOOD"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getAvailableEquipment_ShouldReturnEquipmentList() throws Exception {
        List<Equipment> availableEquipment = Arrays.asList(testEquipment);
        when(equipmentService.getAvailableEquipment()).thenReturn(availableEquipment);

        mockMvc.perform(get("/api/equipment/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Camera"))
                .andExpect(jsonPath("$[0].category").value("Video"));
    }

    @Test
    void searchEquipment_ShouldReturnFilteredEquipment() throws Exception {
        List<Equipment> filteredEquipment = Arrays.asList(testEquipment);
        when(equipmentService.searchEquipment("Video")).thenReturn(filteredEquipment);

        mockMvc.perform(get("/api/equipment/search")
                .param("category", "Video"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Camera"))
                .andExpect(jsonPath("$[0].category").value("Video"));
    }
}