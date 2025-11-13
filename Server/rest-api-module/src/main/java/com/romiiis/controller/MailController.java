package com.romiiis.controller;

import com.romiiis.domain.Project;
import com.romiiis.event.AdminMessageEvent;
import com.romiiis.model.SendEmailRequestDTO;
import com.romiiis.port.IDomainEventPublisher;
import com.romiiis.service.api.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class MailController extends AbstractController implements MailsApi {


    private final IProjectService projectService;
    private final IDomainEventPublisher eventPublisher;


    /**
     * Sends an email to the customer regarding a specific project.
     *
     * @param sendEmailRequestDTO the request DTO containing project ID and email text
     * @return ResponseEntity indicating the result of the operation
     */
    @Override
    public ResponseEntity<Void> sendEmail(SendEmailRequestDTO sendEmailRequestDTO) {

        Project project = projectService.getProjectById(sendEmailRequestDTO.getProjectId());

        eventPublisher.publish(new AdminMessageEvent(
                project,
                sendEmailRequestDTO.getCustomer(),
                sendEmailRequestDTO.getTranslator(),
                sendEmailRequestDTO.getText()
        ));

        return ResponseEntity.ok().build();
    }
}
