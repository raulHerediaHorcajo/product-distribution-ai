package com.productdistribution.backend.mappers;

import org.mapstruct.Mapper;

import com.productdistribution.backend.dtos.WarehouseDTO;
import com.productdistribution.backend.entities.Warehouse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseDTO toDTO(Warehouse entity);
    Warehouse toEntity(WarehouseDTO dto);
    List<WarehouseDTO> toDTOList(List<Warehouse> entities);
    List<Warehouse> toEntityList(List<WarehouseDTO> dtos);
}