package com.romiiis.controller;

import com.romiiis.configuration.ProjectsFilter;
import com.romiiis.exception.BaseException;
import com.romiiis.mapper.CommonMapper;
import com.romiiis.mapper.ProjectMapper;
import com.romiiis.model.ProjectDTO;
import com.romiiis.model.ProjectStateDTO;
import com.romiiis.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Controller for project-related endpoints
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProjectController implements ProjectsApi {

    /**
     * Services
     */
    private final IProjectService projectService;
    private final ProjectMapper projectMapper;
    private final CommonMapper commonMapper;


    /**
     * Lists all projects with optional filtering by state, language code, and feedback presence.
     *
     * @param state        Filter projects by their state. (optional)
     * @param languageCode (optional)
     * @param hasFeedback  (optional)
     * @return A ResponseEntity containing a list of ProjectDTOs.
     */
    @Override
    public ResponseEntity<List<ProjectDTO>> listAllProjects(ProjectStateDTO state, String languageCode, Boolean hasFeedback) {
        ProjectsFilter filter = new ProjectsFilter().setHasFeedback(hasFeedback).setLanguageCode(languageCode).setStatus(commonMapper.mapProjectStateDTOToDomain(state));

        try {
            var projects = projectService.getAllProjects(filter);
            return new ResponseEntity<>(projectMapper.mapDomainListToDTO(projects), HttpStatus.OK);

        } catch (BaseException e) {
            log.error("Error during listing all projects ({}): {}", e.getHttpStatus().getCode(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.valueOf(e.getHttpStatus().getCode()));

        } catch (Exception e) {
            log.error("Error during listing all projects (500): {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<ProjectDTO> createProject(String languageCode, MultipartFile content, UUID customerId) {
        try {

            Locale language = Locale.forLanguageTag(languageCode);

            var newProject = projectService.createProject(
                    customerId,
                    language,
                    content.getBytes()
            );

            var projectDTO = projectMapper.mapDomainToDTO(newProject);
            return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
        } catch (BaseException e) {
            log.error("Error during project creation ({}): {}", e.getHttpStatus().getCode(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.valueOf(e.getHttpStatus().getCode()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





}
