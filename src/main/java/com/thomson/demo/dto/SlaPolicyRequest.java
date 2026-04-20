package com.thomson.demo.dto;

import com.thomson.demo.enums.Priority;
import lombok.Data;

@Data
public class SlaPolicyRequest {
    private Priority priority;
    private int responseTimeHours;
    private int resolutionTimeHours;
}
