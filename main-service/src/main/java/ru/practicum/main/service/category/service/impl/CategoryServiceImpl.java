package ru.practicum.main.service.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.category.dto.CategoryDto;
import ru.practicum.main.service.category.mapper.CategoryMapper;
import ru.practicum.main.service.category.model.Category;
import ru.practicum.main.service.category.repository.CategoryRepository;
import ru.practicum.main.service.category.service.CategoryService;
import ru.practicum.main.service.exception.CategoryNotFoundException;
import ru.practicum.main.service.exception.CategoryNameAlreadyExistsException;
import ru.practicum.main.service.exception.OperationConditionsNotMetException;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Создание новой категории с именем: {}", categoryDto.getName());

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new CategoryNameAlreadyExistsException("Категория с именем '" + categoryDto.getName() + "' уже существует");
        }

        Category category = CategoryMapper.toEntity(categoryDto);
        try {
            category = categoryRepository.save(category);
            log.info("Категория успешно создана с id: {}", category.getId());
            return CategoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameAlreadyExistsException("Категория с именем '" + categoryDto.getName() + "' уже существует");
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Обновление категории с id: {}", catId);

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Категория с id=" + catId + " не найдена"));

        if (!category.getName().equals(categoryDto.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new CategoryNameAlreadyExistsException("Категория с именем '" + categoryDto.getName() + "' уже существует");
        }

        category.setName(categoryDto.getName());

        try {
            category = categoryRepository.save(category);
            log.info("Категория с id: {} успешно обновлена", catId);
            return CategoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameAlreadyExistsException("Категория с именем '" + categoryDto.getName() + "' уже существует");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Удаление категории с id: {}", catId);

        if (!categoryRepository.existsById(catId)) {
            throw new CategoryNotFoundException("Категория с id=" + catId + " не найдена");
        }

        try {
            categoryRepository.deleteById(catId);
            log.info("Категория с id: {} успешно удалена", catId);
        } catch (DataIntegrityViolationException e) {
            throw new OperationConditionsNotMetException(
                    "Невозможно удалить категорию с id=" + catId + ", так как она связана с существующими событиями"
            );
        }
    }
}