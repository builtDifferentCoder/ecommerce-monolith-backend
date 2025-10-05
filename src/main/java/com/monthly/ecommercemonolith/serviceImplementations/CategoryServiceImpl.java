package com.monthly.ecommercemonolith.serviceImplementations;

import com.monthly.ecommercemonolith.entities.Category;
import com.monthly.ecommercemonolith.exceptions.APIException;
import com.monthly.ecommercemonolith.exceptions.ResourceNotFoundException;
import com.monthly.ecommercemonolith.payload.CategoryDTO;
import com.monthly.ecommercemonolith.payload.CategoryResponse;
import com.monthly.ecommercemonolith.repositories.CategoryRepository;
import com.monthly.ecommercemonolith.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,
                                             Integer pageSize, String sortBy,
                                             String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty())
            throw new APIException("No category created " +
                    "till now.");
        List<CategoryDTO> categoryDTOS =
                categories.stream().map(category -> modelMapper.map(category,
                        CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory =
                categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) throw new APIException("Category with the " +
                "name" + category.getCategoryName() + " already exists.");
        Category newCategory = categoryRepository.save(category);
        CategoryDTO newCategoryDto;
        newCategoryDto = modelMapper.map(newCategory,
                CategoryDTO.class);
        return newCategoryDto;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category =
                categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", categoryId, "categoryId"));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategory =
                categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", categoryId, "categoryId"));
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
