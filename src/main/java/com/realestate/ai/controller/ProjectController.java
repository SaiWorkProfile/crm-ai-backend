package com.realestate.ai.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.ai.model.Project;
import com.realestate.ai.service.ProjectService;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/admin/projects")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public Project create(@RequestBody Project p) {
        return service.create(p);
    }

    @GetMapping
    public List<Project> all() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project p) {
        return service.update(id, p);
    }

    @PostMapping("/{id}/archive")
    public void archive(@PathVariable Long id) {
        service.archive(id);
    }

    @PostMapping("/{id}/publish")
    public void publish(@PathVariable Long id) {
        service.publish(id);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

}
