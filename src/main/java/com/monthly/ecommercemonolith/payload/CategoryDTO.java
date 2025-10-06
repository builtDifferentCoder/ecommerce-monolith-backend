package com.monthly.ecommercemonolith.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @Schema(description = "Category id for a particular category", example =
            "101")
    private Long categoryId;
    @Schema(description = "Category name for a category you want to create",
            example = "Sports")
    private String categoryName;
}
