package com.thomson.demo.dto;

import com.thomson.demo.enums.Priority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlaPolicyResponse {
    private Long id;
    private Priority priority;
    private int responseTimeHours;
    private int resolutionTimeHours;
}
