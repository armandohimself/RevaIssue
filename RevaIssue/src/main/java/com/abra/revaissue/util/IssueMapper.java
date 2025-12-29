package com.abra.revaissue.util;

import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.IssueResponseDTO;
import com.abra.revaissue.dto.IssueUpdateDTO;
import com.abra.revaissue.entity.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {
    public Issue toIssue(IssueCreateDTO dto){
        Issue issue = new Issue();
        issue.setName(dto.getName());
        issue.setDescription(dto.getDescription());
        issue.setSeverity(dto.getSeverity());
        issue.setPriority(dto.getPriority());
        return issue;
    }
    public void updateEntity(IssueUpdateDTO dto, Issue issue){
        if(dto.getName() != null){
            issue.setName(dto.getName());
        }
        if(dto.getDescription() != null){
            issue.setDescription(dto.getDescription());
        }
        if(dto.getSeverity() != null){
            issue.setSeverity(dto.getSeverity());
        }
        if(dto.getPriority() != null){
            issue.setPriority(dto.getPriority());
        }
    }
    public IssueResponseDTO toResponseDTO(Issue issue){
        IssueResponseDTO dto = new IssueResponseDTO();
        dto.setIssueId(issue.getIssueId());
        dto.setName(issue.getName());
        dto.setDescription(issue.getDescription());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());
        dto.setSeverity(issue.getSeverity());
        return dto;
    }
}
