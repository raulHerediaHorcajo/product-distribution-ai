package com.productdistribution.backend.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.controllers.WarehouseController;
import com.productdistribution.backend.dtos.WarehouseDTO;
import com.productdistribution.backend.entities.Warehouse;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.mappers.WarehouseMapper;
import com.productdistribution.backend.services.WarehouseService;
import com.productdistribution.backend.utils.WarehouseBuilder;
import com.productdistribution.backend.utils.WarehouseDTOBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WarehouseController.class)
@ActiveProfiles("test")
class WarehouseControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WarehouseService warehouseService;

    @MockitoBean
    private WarehouseMapper warehouseMapper;

    @Test
    void getAllWarehouses_shouldReturn200WithWarehouseList() throws Exception {
        List<Warehouse> warehouses = List.of(WarehouseBuilder.warehouse1(), WarehouseBuilder.warehouse2());
        List<WarehouseDTO> dtos = List.of(WarehouseDTOBuilder.warehouseDTO1(), WarehouseDTOBuilder.warehouseDTO2());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(warehouseService.getAllWarehouses()).thenReturn(warehouses);
        when(warehouseMapper.toDTOList(warehouses)).thenReturn(dtos);

        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(warehouseService).getAllWarehouses();
        verify(warehouseMapper).toDTOList(warehouses);
    }

    @Test
    void getWarehouseById_whenWarehouseExists_shouldReturn200WithWarehouse() throws Exception {
        String warehouseId = "W1";
        Warehouse warehouse = WarehouseBuilder.warehouse1();
        WarehouseDTO dto = WarehouseDTOBuilder.warehouseDTO1();
        String expectedJson = objectMapper.writeValueAsString(dto);

        when(warehouseService.getWarehouseById(warehouseId)).thenReturn(warehouse);
        when(warehouseMapper.toDTO(warehouse)).thenReturn(dto);

        mockMvc.perform(get("/api/warehouses/{id}", warehouseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(warehouseService).getWarehouseById(warehouseId);
        verify(warehouseMapper).toDTO(warehouse);
    }

    @Test
    void getWarehouseById_whenWarehouseNotFound_shouldReturn404() throws Exception {
        String warehouseId = "W999";

        when(warehouseService.getWarehouseById(warehouseId))
                .thenThrow(new ResourceNotFoundException("Warehouse", warehouseId));

        mockMvc.perform(get("/api/warehouses/{id}", warehouseId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Warehouse with id 'W999' not found"))
                .andExpect(jsonPath("$.path").value("/api/warehouses/W999"));

        verify(warehouseService).getWarehouseById(warehouseId);
        verify(warehouseMapper, never()).toDTO(any(Warehouse.class));
    }
}