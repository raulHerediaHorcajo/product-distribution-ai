package com.productdistribution.backend.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdistribution.backend.controllers.StoreController;
import com.productdistribution.backend.dtos.StoreDTO;
import com.productdistribution.backend.entities.Store;
import com.productdistribution.backend.exceptions.ResourceNotFoundException;
import com.productdistribution.backend.mappers.StoreMapper;
import com.productdistribution.backend.services.StoreService;
import com.productdistribution.backend.utils.StoreBuilder;
import com.productdistribution.backend.utils.StoreDTOBuilder;
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

@WebMvcTest(StoreController.class)
@ActiveProfiles("test")
class StoreControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private StoreMapper storeMapper;

    @Test
    void getAllStores_shouldReturn200WithStoreList() throws Exception {
        List<Store> stores = List.of(StoreBuilder.store1(), StoreBuilder.store2());
        List<StoreDTO> dtos = List.of(StoreDTOBuilder.storeDTO1(), StoreDTOBuilder.storeDTO2());
        String expectedJson = objectMapper.writeValueAsString(dtos);

        when(storeService.getAllStores()).thenReturn(stores);
        when(storeMapper.toDTOList(stores)).thenReturn(dtos);

        mockMvc.perform(get("/api/stores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(storeService).getAllStores();
        verify(storeMapper).toDTOList(stores);
    }

    @Test
    void getStoreById_whenStoreExists_shouldReturn200WithStore() throws Exception {
        String storeId = "S1";
        Store store = StoreBuilder.store1();
        StoreDTO dto = StoreDTOBuilder.storeDTO1();
        String expectedJson = objectMapper.writeValueAsString(dto);

        when(storeService.getStoreById(storeId)).thenReturn(store);
        when(storeMapper.toDTO(store)).thenReturn(dto);

        mockMvc.perform(get("/api/stores/{id}", storeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(storeService).getStoreById(storeId);
        verify(storeMapper).toDTO(store);
    }

    @Test
    void getStoreById_whenStoreNotFound_shouldReturn404() throws Exception {
        String storeId = "S999";

        when(storeService.getStoreById(storeId))
                .thenThrow(new ResourceNotFoundException("Store", storeId));

        mockMvc.perform(get("/api/stores/{id}", storeId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Store with id 'S999' not found"))
                .andExpect(jsonPath("$.path").value("/api/stores/S999"));

        verify(storeService).getStoreById(storeId);
        verify(storeMapper, never()).toDTO(any(Store.class));
    }
}