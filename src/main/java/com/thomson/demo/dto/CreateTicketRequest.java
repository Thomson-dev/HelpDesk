package com.thomson.demo.dto;

import com.thomson.demo.enums.IssueCategory;
import lombok.Data;

@Data
public class CreateTicketRequest {
    private String title;
    private String description;
    private IssueCategory category;
}
