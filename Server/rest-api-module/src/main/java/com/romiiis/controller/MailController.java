package com.romiiis.controller;

import com.romiiis.domain.Project;
import com.romiiis.model.SendEmailRequestDTO;
import com.romiiis.service.interfaces.IMailService;
import com.romiiis.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling mail-related operations.
 *
 * @author Roman Pejs
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class MailController implements MailsApi {

    @Value("${mail.adminMessageSubject}")
    private final String ADMIN_SUBJECT_TEMPLATE = "Admin reaction for project with id: %s (file: %s)";


    private final IMailService mailService;
    private final IProjectService projectService;



    /**
     * Sends an email to the customer regarding a specific project.
     *
     * @param sendEmailRequestDTO the request DTO containing project ID and email text
     * @return ResponseEntity indicating the result of the operation
     */
    @Override
    public ResponseEntity<Void> sendEmail(SendEmailRequestDTO sendEmailRequestDTO) {


        Project project = projectService.getProjectById(sendEmailRequestDTO.getProjectId());
        String subject = String.format(ADMIN_SUBJECT_TEMPLATE, sendEmailRequestDTO.getProjectId(), project.getOriginalFileName());


        if (sendEmailRequestDTO.getCustomer()) {
            mailService.sendEmailToCustomer(project, subject, sendEmailRequestDTO.getText());
        }

        if (sendEmailRequestDTO.getTranslator()) {
            mailService.sendEmailToTranslator(project, subject, sendEmailRequestDTO.getText());
        }

        return ResponseEntity.ok().build();
    }
}
