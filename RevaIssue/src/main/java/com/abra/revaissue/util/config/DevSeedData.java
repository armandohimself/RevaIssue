package com.abra.revaissue.util.config;
import java.time.Instant;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.repository.ProjectRepository;

@Component
@Profile("dev") // only runs when dev profile is active
public class DevSeedData implements CommandLineRunner {

    private final ProjectRepository projectRepository;

    public DevSeedData(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) {
        if (projectRepository.count() > 0) return; // don’t reseed every time

        Project p = new Project();
        p.setProjectName("Seed Project 1");
        p.setProjectDescription("Generated at startup");
        p.setCreatedByUserId(UUID.randomUUID());
        p.setCreatedAt(Instant.now());

        projectRepository.save(p);
    }
}