package com.abra.revaissue.dto;

import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import lombok.Data;
@Data
public class IssueUpdateDTO {
    private String name;
    private String description;
    private IssueSeverity severity;
    private IssuePriority priority;
}
