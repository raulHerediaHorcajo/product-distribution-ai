package com.productdistribution.backend.unit.mappers;

import com.productdistribution.backend.dtos.ProductDTO;
import com.productdistribution.backend.entities.Product;
import com.productdistribution.backend.mappers.ProductMapper;
import com.productdistribution.backend.utils.ProductBuilder;
import com.productdistribution.backend.utils.ProductDTOBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperUnitTest {

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toDTO_shouldReturnDTO() {
        Product product = ProductBuilder.product1();
        ProductDTO expected = ProductDTOBuilder.productDTO1();

        ProductDTO dto = productMapper.toDTO(product);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(expected.id());
        assertThat(dto.brandId()).isEqualTo(expected.brandId());
        assertThat(dto.sizes()).containsExactlyElementsOf(expected.sizes());
    }

    @Test
    void toDTO_whenNull_shouldReturnNull() {
        Product product = null;

        ProductDTO dto = productMapper.toDTO(product);

        assertThat(dto).isNull();
    }

    @Test
    void toDTO_whenSizesIsNull_shouldReturnDTOWithNullSizes() {
        Product product = ProductBuilder.builder().withSizes(null).build();
        ProductDTO expected = ProductDTOBuilder.builder().withSizes(null).build();

        ProductDTO dto = productMapper.toDTO(product);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(expected.id());
        assertThat(dto.brandId()).isEqualTo(expected.brandId());
        assertThat(dto.sizes()).isNull();
    }

    @Test
    void toEntity_shouldReturnEntity() {
        ProductDTO dto = ProductDTOBuilder.productDTO1();
        Product expected = ProductBuilder.product1();

        Product product = productMapper.toEntity(dto);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(expected.getId());
        assertThat(product.getBrandId()).isEqualTo(expected.getBrandId());
        assertThat(product.getSizes()).containsExactlyElementsOf(expected.getSizes());
    }

    @Test
    void toEntity_whenNull_shouldReturnNull() {
        ProductDTO dto = null;

        Product product = productMapper.toEntity(dto);

        assertThat(product).isNull();
    }

    @Test
    void toEntity_whenSizesIsNull_shouldReturnEntityWithNullSizes() {
        ProductDTO dto = ProductDTOBuilder.builder().withSizes(null).build();
        Product expected = ProductBuilder.builder().withSizes(null).build();

        Product product = productMapper.toEntity(dto);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(expected.getId());
        assertThat(product.getBrandId()).isEqualTo(expected.getBrandId());
        assertThat(product.getSizes()).isNull();
    }

    @Test
    void toDTOList_shouldReturnDTOList() {
        List<Product> products = List.of(
            ProductBuilder.product1(),
            ProductBuilder.product2()
        );
        ProductDTO expected1 = ProductDTOBuilder.productDTO1();
        ProductDTO expected2 = ProductDTOBuilder.productDTO2();

        List<ProductDTO> dtos = productMapper.toDTOList(products);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isEqualTo(expected1);
        assertThat(dtos.get(1)).isEqualTo(expected2);
    }

    @Test
    void toDTOList_whenNull_shouldReturnNull() {
        List<Product> products = null;

        List<ProductDTO> dtos = productMapper.toDTOList(products);

        assertThat(dtos).isNull();
    }

    @Test
    void toDTOList_whenEmptyList_shouldReturnEmptyList() {
        List<Product> products = List.of();

        List<ProductDTO> dtos = productMapper.toDTOList(products);

        assertThat(dtos).isEmpty();
    }

    @Test
    void toEntityList_shouldReturnEntityList() {
        List<ProductDTO> dtos = List.of(
            ProductDTOBuilder.productDTO1(),
            ProductDTOBuilder.productDTO2()
        );
        Product expected1 = ProductBuilder.product1();
        Product expected2 = ProductBuilder.product2();

        List<Product> products = productMapper.toEntityList(dtos);

        assertThat(products).hasSize(2);
        assertThat(products.get(0)).usingRecursiveComparison().isEqualTo(expected1);
        assertThat(products.get(1)).usingRecursiveComparison().isEqualTo(expected2);
    }

    @Test
    void toEntityList_whenNull_shouldReturnNull() {
        List<ProductDTO> dtos = null;

        List<Product> products = productMapper.toEntityList(dtos);

        assertThat(products).isNull();
    }

    @Test
    void toEntityList_whenEmptyList_shouldReturnEmptyList() {
        List<ProductDTO> dtos = List.of();

        List<Product> products = productMapper.toEntityList(dtos);

        assertThat(products).isEmpty();
    }
}