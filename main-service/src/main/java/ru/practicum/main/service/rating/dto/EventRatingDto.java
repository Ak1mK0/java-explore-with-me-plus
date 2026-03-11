package ru.practicum.main.service.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRatingDto {
    private Integer eventId;
    private Integer likes;
    private Integer dislikes;
    private Integer rating;
}