package ru.practicum.main.service.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingDto {
    private Integer userId;
    private Integer totalLikes;
    private Integer totalDislikes;
    private Integer totalRating;
}