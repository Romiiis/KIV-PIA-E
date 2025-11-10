package com.romiiis.domain;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class WrapperProjectFeedback {
    private Project project;
    private Feedback feedback;
}