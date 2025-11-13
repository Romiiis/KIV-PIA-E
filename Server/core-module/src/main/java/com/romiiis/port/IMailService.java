package com.romiiis.port;

import com.romiiis.domain.Project;

public interface IMailService {

    /**
     * Sends an email to the customer associated with the given project.
     * @param project the project for which the email is to be sent
     * @param subject the subject of the email
     * @param text the content of the email
     */
    void sendEmailToCustomer(Project project, String subject, String text);


    /**
     * Sends an email to the translator associated with the given project.
     * @param project the project for which the email is to be sent
     * @param subject the subject of the email
     * @param text the content of the email
     */
    void sendEmailToTranslator(Project project, String subject, String text);

}
