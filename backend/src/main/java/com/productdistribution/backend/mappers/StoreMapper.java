package com.productdistribution.backend.mappers;

import org.mapstruct.Mapper;

import com.productdistribution.backend.dtos.StoreDTO;
import com.productdistribution.backend.entities.Store;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    StoreDTO toDTO(Store entity);
    Store toEntity(StoreDTO dto);
    List<StoreDTO> toDTOList(List<Store> entities);
    List<Store> toEntityList(List<StoreDTO> dtos);
}