package ru.practicum.main.service.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.exception.AlreadyExistsException;
import ru.practicum.main.service.user.dto.NewUserRequest;
import ru.practicum.main.service.user.dto.UserDto;
import ru.practicum.main.service.user.mapper.UserMapper;
import ru.practicum.main.service.user.model.User;
import ru.practicum.main.service.user.repository.UserRepository;
import ru.practicum.main.service.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto saveUser(NewUserRequest request) {
        log.info("Создание нового пользователя: {}", request);
        UserEmailCheck(request);
        User user = UserMapper.toEntity(request);
        log.info("Entity: {}", user);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids, Long from, Long size) {
        if (ids != null) {
            log.info("Получение списка пользователей с ids: {}", ids);
            List<UserDto> users = userRepository.getUsers(ids).stream()
                    .map(UserMapper::toDto)
                    .toList();
            log.info("Список по ids: {}", users);
            return users;

        } else {
            log.info("Получение списка из первых {} пользователей с позиции {}: ", size, from);
            List<UserDto> users = userRepository.getUsers(from, size).stream()
                    .map(UserMapper::toDto)
                    .toList();
            log.info("Список подряд: {}", users);
            return users;
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);

        userRepository.deleteById(id);
    }

    private void UserEmailCheck(NewUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Пользователь с адресом '" + request.getEmail() + "' уже существует");
        }
    }

}
