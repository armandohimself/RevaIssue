package com.abra.revaissue.dto;

import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class IssueCreateDTO {
    @NotNull
    private String name;
    private String description;
    @NotNull
    private IssueSeverity severity;
    @NotNull
    private IssuePriority priority;
}
