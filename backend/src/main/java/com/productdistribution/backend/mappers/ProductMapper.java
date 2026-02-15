package com.productdistribution.backend.mappers;

import org.mapstruct.Mapper;

import com.productdistribution.backend.dtos.ProductDTO;
import com.productdistribution.backend.entities.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product entity);
    Product toEntity(ProductDTO dto);
    List<ProductDTO> toDTOList(List<Product> entities);
    List<Product> toEntityList(List<ProductDTO> dtos);
}