package com.abra.revaissue.dto;

import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import lombok.Data;

import java.util.UUID;
@Data
public class IssueResponseDTO {
    private UUID issueId;
    private String name;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private IssueSeverity severity;
}
