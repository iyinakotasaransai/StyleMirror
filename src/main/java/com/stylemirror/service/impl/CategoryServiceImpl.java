package com.stylemirror.service.impl;

import com.stylemirror.dto.CategoryDto;
import com.stylemirror.entity.Category;
import com.stylemirror.exception.BadRequestException;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.CategoryRepository;
import com.stylemirror.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        String name = categoryDto.getName().trim();
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("Category with name '" + name + "' already exists");
        }

        String slug = categoryDto.getSlug() != null && !categoryDto.getSlug().isBlank()
                ? categoryDto.getSlug().trim()
                : generateSlug(name);

        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .description(categoryDto.getDescription())
                .active(categoryDto.getActive() != null ? categoryDto.getActive() : true)
                .build();

        return mapToDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setName(categoryDto.getName().trim());
        if (categoryDto.getSlug() != null && !categoryDto.getSlug().isBlank()) {
            category.setSlug(categoryDto.getSlug().trim());
        }
        category.setDescription(categoryDto.getDescription());
        if (categoryDto.getActive() != null) {
            category.setActive(categoryDto.getActive());
        }

        return mapToDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryDto mapToDto(Category entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

    private String generateSlug(String input) {
        return input.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
