package com.productdistribution.backend.mappers;

import org.mapstruct.Mapper;

import com.productdistribution.backend.dtos.StockAssignmentDTO;
import com.productdistribution.backend.entities.StockAssignment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockAssignmentMapper {
    StockAssignmentDTO toDTO(StockAssignment entity);
    StockAssignment toEntity(StockAssignmentDTO dto);
    List<StockAssignmentDTO> toDTOList(List<StockAssignment> entities);
    List<StockAssignment> toEntityList(List<StockAssignmentDTO> dtos);
}