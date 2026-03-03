package ru.practicum.main.service.user.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @Email(message = "Поле email обязательно")
    private String email;

    @NotBlank(message = "Имя пользователя обязательно")
    private String name;
}
