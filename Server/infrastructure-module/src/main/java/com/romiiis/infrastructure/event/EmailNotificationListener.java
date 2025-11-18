package com.romiiis.infrastructure.event;

import com.romiiis.event.*;
import com.romiiis.port.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationListener {

    // Services
    private final IMailService emailService;

    // Email templates
    @Value("${mail.template.approved.subject}")
    private String approvedSubjectTemplate;

    @Value("${mail.template.approved.body}")
    private String approvedBodyTemplate;

    @Value("${mail.template.rejected.subject}")
    private String rejectedSubjectTemplate;

    @Value("${mail.template.rejected.body}")
    private String rejectedBodyTemplate;

    @Value("${mail.template.completed.subject}")
    private String completedSubjectTemplate;

    @Value("${mail.template.completed.body}")
    private String completedBodyTemplate;

    @Value("${mail.template.admin-message.subject}")
    private String adminSubjectTemplate;

    @Value("${mail.template.admin-message.body}")
    private String adminBodyTemplate;

    @Value("${mail.template.closed.subject}")
    private String closedSubjectTemplate;

    @Value("${mail.template.closed.body}")
    private String closedBodyTemplate;


    @Value("${mail.template.translator-assigned.subject}")
    private String translatorAssignedSubjectTemplate;

    @Value("${mail.template.translator-assigned.body}")
    private String translatorAssignedBodyTemplate;

    @Value("${mail.template.newproject.customer.subject}")
    private String newProjectCustomerSubjectTemplate;

    @Value("${mail.template.newproject.customer.body}")
    private String newProjectCustomerBodyTemplate;

    @Value("${mail.template.newproject.notransalator.subject}")
    private String newProjectNoTranslatorSubjectTemplate;

    @Value("${mail.template.newproject.notransalator.body}")
    private String newProjectNoTranslatorBodyTemplate;


    /**
     * Handles the AdminMessageEvent by sending emails to the customer and/or translator based on the event details.
     *
     * @param event the AdminMessageEvent containing project and message details
     */
    @Async
    @TransactionalEventListener
    public void handleAdminMessageEvent(AdminMessageEvent event) {

        // Prepare subject and body using templates
        String subject = String.format(
                adminSubjectTemplate,
                event.project().getOriginalFileName()
        );

        // Prepare body using template
        String body = String.format(
                adminBodyTemplate,
                event.project().getOriginalFileName(),
                event.text()
        );

        log.info("Sending admin message email for project id: {}", event.project().getId());

        // Send emails based on event flags
        if (event.sendToCustomer()) {
            emailService.sendEmailToCustomer(event.project(), subject, body);
        }

        if (event.sendToTranslator()) {
            emailService.sendEmailToTranslator(event.project(), subject, body);
        }


    }


    /**
     * Handles the ProjectApprovedEvent by sending an approval email to the translator.
     *
     * @param event the ProjectApprovedEvent containing project details
     */
    @Async
    @TransactionalEventListener
    public void handleProjectApproved(ProjectApprovedEvent event) {
        String subject = String.format(approvedSubjectTemplate, event.project().getOriginalFileName());
        String body = String.format(approvedBodyTemplate, event.project().getOriginalFileName());

        log.info("Sending project approved email for project id: {}", event.project().getId());

        emailService.sendEmailToTranslator(event.project(), subject, body);
    }


    /**
     * Handles the ProjectRejectedEvent.
     *
     * @param event the ProjectRejectedEvent containing project details
     */
    @Async
    @TransactionalEventListener
    public void handleProjectRejected(ProjectRejectedEvent event) {
        String subject = String.format(rejectedSubjectTemplate, event.project().getOriginalFileName());
        String body = String.format(rejectedBodyTemplate, event.project().getOriginalFileName(), event.reason());

        log.info("Sending project rejected email for project id: {}", event.project().getId());

        emailService.sendEmailToTranslator(event.project(), subject, body);
    }

    @Async
    @TransactionalEventListener
    public void handleProjectCompleted(ProjectCompletedEvent event) {
        String subject = String.format(completedSubjectTemplate, event.project().getOriginalFileName());
        String body = String.format(completedBodyTemplate, event.project().getOriginalFileName());

        log.info("Sending project completed email for project id: {}", event.project().getId());

        emailService.sendEmailToCustomer(event.project(), subject, body);
    }


    @Async
    @TransactionalEventListener
    public void handleProjectClosed(ProjectClosedEvent event) {
        String subject = String.format(closedSubjectTemplate, event.project().getOriginalFileName());
        String body = String.format(closedBodyTemplate, event.project().getOriginalFileName());

        log.info("Sending project closed email for project id: {}", event.project().getId());

        emailService.sendEmailToCustomer(event.project(), subject, body);
        emailService.sendEmailToTranslator(event.project(), subject, body);
    }


    @Async
    @TransactionalEventListener
    public void handleProjectCreationSuccess(TranslatorAssignedToProjectEvent event) {
        String subject = String.format(translatorAssignedSubjectTemplate, event.project().getOriginalFileName());
        String body = String.format(translatorAssignedBodyTemplate, event.project().getOriginalFileName());

        log.info("Sending new project assignment email for project id: {}", event.project().getId());

        emailService.sendEmailToTranslator(event.project(), subject, body);


        String customerSubject = String.format(newProjectCustomerSubjectTemplate, event.project().getOriginalFileName());
        String customerBody = String.format(newProjectCustomerBodyTemplate, event.project().getOriginalFileName());

        emailService.sendEmailToCustomer(event.project(), customerSubject, customerBody);


    }

    @Async
    @TransactionalEventListener
    public void handleProjectCreationNoTranslator(NoTranslatorAssignedToProjectEvent event) {
        String subject = String.format(newProjectNoTranslatorSubjectTemplate, event.project().getOriginalFileName());
        String body = String.format(newProjectNoTranslatorBodyTemplate, event.project().getOriginalFileName());

        log.info("Sending new project (no translator) email for project id: {}", event.project().getId());

        emailService.sendEmailToCustomer(event.project(), subject, body);
    }





}
