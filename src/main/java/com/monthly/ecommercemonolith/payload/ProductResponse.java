package com.monthly.ecommercemonolith.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private List<ProductDTO> content;
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Long totalElements;
    private Boolean lastPage;
}
