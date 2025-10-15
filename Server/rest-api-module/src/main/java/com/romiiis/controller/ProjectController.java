package com.romiiis.controller;

import com.romiiis.model.CreateProjectRequestDTO;
import com.romiiis.model.CreateProjectResponseDTO;
import com.romiiis.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class ProjectController implements ProjectsApi {

    private final IProjectService IProjectService;

    @Override
    public ResponseEntity<CreateProjectResponseDTO> createProject(CreateProjectRequestDTO createProjectRequestDTO) {
        try {
            // map input
            var targetLanguage = Locale.forLanguageTag(createProjectRequestDTO.getLanguageCode());
            var sourceFile = createProjectRequestDTO.getOriginalFile().getContentAsByteArray();

            // call core service
            var createdProject = IProjectService.createProject(targetLanguage, sourceFile);

            // map output
            var createdProjectDTO = new CreateProjectResponseDTO()
                    .id(createdProject.getId())
                    .languageCode(createdProject.getTargetLanguage().toLanguageTag())
                    .originalFile(new ByteArrayResource(createdProject.getSourceFile()))
                    .state(CreateProjectResponseDTO.StateEnum.valueOf(createdProject.getState().toString()))
                    .createdAt(createdProject.getCreatedAt().atOffset(ZoneOffset.UTC));

            return new ResponseEntity<>(createdProjectDTO, HttpStatus.CREATED);

        } catch (IOException e) {
            // TODO: log the exception

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // TODO: log the exception

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
