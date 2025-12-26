package com.abra.revaissue.service;

import com.abra.revaissue.repository.ProjectMemberRepository;
import com.abra.revaissue.repository.ProjectRepository;

import org.springframework.stereotype.Service;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
    }

    

}
