package com.realestate.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.realestate.ai.model.Project;
import com.realestate.ai.repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public Project create(Project p) {
        return repo.save(p);
    }

    public List<Project> getAll() {
        return repo.findAll();
    }

    public Project update(Long id, Project p) {
        Project existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        existing.setName(p.getName());
        existing.setType(p.getType());
        existing.setLocation(p.getLocation());
        existing.setReraNumber(p.getReraNumber());

        return repo.save(existing);
    }

    public void archive(Long id) {
        Project p = repo.findById(id).orElseThrow();
        p.setStatus("ARCHIVED");
        repo.save(p);
    }

    public void publish(Long id) {
        Project p = repo.findById(id).orElseThrow();
        p.setPublished(true);
        repo.save(p);
    }
    public void delete(Long id){

        if(!repo.existsById(id)){
            throw new RuntimeException("Project Not Found");
        }

        repo.deleteById(id);
    }



}
