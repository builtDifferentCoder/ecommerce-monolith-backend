package com.monthly.ecommercemonolith.services;

import com.monthly.ecommercemonolith.payload.CategoryDTO;
import com.monthly.ecommercemonolith.payload.CategoryResponse;



public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,
                                      String sortBy,String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO,Long categoryId);
}
