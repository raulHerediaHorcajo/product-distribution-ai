package com.productdistribution.backend.unit.mappers;

import com.productdistribution.backend.dtos.StockAssignmentDTO;
import com.productdistribution.backend.entities.StockAssignment;
import com.productdistribution.backend.mappers.StockAssignmentMapper;
import com.productdistribution.backend.utils.StockAssignmentBuilder;
import com.productdistribution.backend.utils.StockAssignmentDTOBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StockAssignmentMapperUnitTest {

    private final StockAssignmentMapper stockAssignmentMapper = Mappers.getMapper(StockAssignmentMapper.class);

    @Test
    void toDTO_shouldReturnDTO() {
        StockAssignment stockAssignment = StockAssignmentBuilder.stockAssignment1();
        StockAssignmentDTO expected = StockAssignmentDTOBuilder.stockAssignmentDTO1();

        StockAssignmentDTO dto = stockAssignmentMapper.toDTO(stockAssignment);

        assertThat(dto).isEqualTo(expected);
    }

    @Test
    void toDTO_whenNull_shouldReturnNull() {
        StockAssignment stockAssignment = null;

        StockAssignmentDTO dto = stockAssignmentMapper.toDTO(stockAssignment);

        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldReturnEntity() {
        StockAssignmentDTO dto = StockAssignmentDTOBuilder.stockAssignmentDTO1();
        StockAssignment expected = StockAssignmentBuilder.stockAssignment1();

        StockAssignment stockAssignment = stockAssignmentMapper.toEntity(dto);

        assertThat(stockAssignment).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toEntity_whenNull_shouldReturnNull() {
        StockAssignmentDTO dto = null;

        StockAssignment stockAssignment = stockAssignmentMapper.toEntity(dto);

        assertThat(stockAssignment).isNull();
    }

    @Test
    void toDTOList_shouldReturnDTOList() {
        List<StockAssignment> stockAssignments = List.of(
            StockAssignmentBuilder.stockAssignment1(),
            StockAssignmentBuilder.stockAssignment2()
        );
        StockAssignmentDTO expected1 = StockAssignmentDTOBuilder.stockAssignmentDTO1();
        StockAssignmentDTO expected2 = StockAssignmentDTOBuilder.stockAssignmentDTO2();

        List<StockAssignmentDTO> dtos = stockAssignmentMapper.toDTOList(stockAssignments);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isEqualTo(expected1);
        assertThat(dtos.get(1)).isEqualTo(expected2);
    }

    @Test
    void toDTOList_whenNull_shouldReturnNull() {
        List<StockAssignment> stockAssignments = null;

        List<StockAssignmentDTO> dtos = stockAssignmentMapper.toDTOList(stockAssignments);

        assertThat(dtos).isNull();
    }

    @Test
    void toDTOList_whenEmptyList_shouldReturnEmptyList() {
        List<StockAssignment> stockAssignments = List.of();

        List<StockAssignmentDTO> dtos = stockAssignmentMapper.toDTOList(stockAssignments);

        assertThat(dtos).isEmpty();
    }

    @Test
    void toEntityList_shouldReturnEntityList() {
        List<StockAssignmentDTO> dtos = List.of(
            StockAssignmentDTOBuilder.stockAssignmentDTO1(),
            StockAssignmentDTOBuilder.stockAssignmentDTO2()
        );
        StockAssignment expected1 = StockAssignmentBuilder.stockAssignment1();
        StockAssignment expected2 = StockAssignmentBuilder.stockAssignment2();

        List<StockAssignment> stockAssignments = stockAssignmentMapper.toEntityList(dtos);

        assertThat(stockAssignments).hasSize(2);
        assertThat(stockAssignments.get(0)).usingRecursiveComparison().isEqualTo(expected1);
        assertThat(stockAssignments.get(1)).usingRecursiveComparison().isEqualTo(expected2);
    }

    @Test
    void toEntityList_whenNull_shouldReturnNull() {
        List<StockAssignmentDTO> dtos = null;

        List<StockAssignment> stockAssignments = stockAssignmentMapper.toEntityList(dtos);

        assertThat(stockAssignments).isNull();
    }

    @Test
    void toEntityList_whenEmptyList_shouldReturnEmptyList() {
        List<StockAssignmentDTO> dtos = List.of();

        List<StockAssignment> stockAssignments = stockAssignmentMapper.toEntityList(dtos);

        assertThat(stockAssignments).isEmpty();
    }
}