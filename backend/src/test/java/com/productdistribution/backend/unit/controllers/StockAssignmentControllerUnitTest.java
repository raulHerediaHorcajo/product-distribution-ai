package com.productdistribution.backend.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.controllers.StockAssignmentController;
import com.productdistribution.backend.dtos.StockAssignmentDTO;
import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.mappers.StockAssignmentMapper;
import com.productdistribution.backend.repositories.criteria.StockAssignmentCriteria;
import com.productdistribution.backend.services.DistributionService;
import com.productdistribution.backend.services.StockAssignmentService;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StockAssignmentDTOBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockAssignmentController.class)
@ActiveProfiles("test")
class StockAssignmentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DistributionService distributionService;

    @MockitoBean
    private StockAssignmentService stockAssignmentService;

    @MockitoBean
    private StockAssignmentMapper stockAssignmentMapper;

    @Test
    void distributeProducts_shouldReturn200WithStockAssignmentList() throws Exception {
        List<StockAssignment> assignments = List.of(
                StockAssignmentBuilder.stockAssignment1(),
                StockAssignmentBuilder.stockAssignment2()
        );
        List<StockAssignmentDTO> dtos = List.of(
                StockAssignmentDTOBuilder.stockAssignmentDTO1(),
                StockAssignmentDTOBuilder.stockAssignmentDTO2()
        );
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(distributionService.distributeProducts()).thenReturn(assignments);
        when(stockAssignmentMapper.toDTOList(assignments)).thenReturn(dtos);

        mockMvc.perform(post("/api/stock-assignments/distribute"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(distributionService).distributeProducts();
        verify(stockAssignmentMapper).toDTOList(assignments);
    }

    @Test
    void getAssignments_shouldReturn200WithStockAssignmentList() throws Exception {
        List<StockAssignment> assignments = List.of(StockAssignmentBuilder.stockAssignment1());
        List<StockAssignmentDTO> dtos = List.of(StockAssignmentDTOBuilder.stockAssignmentDTO1());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(stockAssignmentService.getAssignments(any(StockAssignmentCriteria.class))).thenReturn(assignments);
        when(stockAssignmentMapper.toDTOList(assignments)).thenReturn(dtos);

        mockMvc.perform(get("/api/stock-assignments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(stockAssignmentService).getAssignments(any(StockAssignmentCriteria.class));
        verify(stockAssignmentMapper).toDTOList(assignments);
    }

    @Test
    void getAssignmentsByStore_shouldReturn200WithStockAssignmentList() throws Exception {
        String storeId = "S1";
        List<StockAssignment> assignments = List.of(StockAssignmentBuilder.stockAssignment1());
        List<StockAssignmentDTO> dtos = List.of(StockAssignmentDTOBuilder.stockAssignmentDTO1());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(stockAssignmentService.getByStoreId(storeId)).thenReturn(assignments);
        when(stockAssignmentMapper.toDTOList(assignments)).thenReturn(dtos);

        mockMvc.perform(get("/api/stock-assignments/stores/{storeId}", storeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(stockAssignmentService).getByStoreId(storeId);
        verify(stockAssignmentMapper).toDTOList(assignments);
    }

    @Test
    void getAssignmentsByProduct_shouldReturn200WithStockAssignmentList() throws Exception {
        String productId = "P1";
        List<StockAssignment> assignments = List.of(StockAssignmentBuilder.stockAssignment1());
        List<StockAssignmentDTO> dtos = List.of(StockAssignmentDTOBuilder.stockAssignmentDTO1());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(stockAssignmentService.getByProductId(productId)).thenReturn(assignments);
        when(stockAssignmentMapper.toDTOList(assignments)).thenReturn(dtos);

        mockMvc.perform(get("/api/stock-assignments/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(stockAssignmentService).getByProductId(productId);
        verify(stockAssignmentMapper).toDTOList(assignments);
    }

    @Test
    void getAssignmentsByWarehouse_shouldReturn200WithStockAssignmentList() throws Exception {
        String warehouseId = "W1";
        List<StockAssignment> assignments = List.of(StockAssignmentBuilder.stockAssignment1());
        List<StockAssignmentDTO> dtos = List.of(StockAssignmentDTOBuilder.stockAssignmentDTO1());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(stockAssignmentService.getByWarehouseId(warehouseId)).thenReturn(assignments);
        when(stockAssignmentMapper.toDTOList(assignments)).thenReturn(dtos);

        mockMvc.perform(get("/api/stock-assignments/warehouses/{warehouseId}", warehouseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(stockAssignmentService).getByWarehouseId(warehouseId);
        verify(stockAssignmentMapper).toDTOList(assignments);
    }
}