package com.romiiis.service.api;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.exception.NoAccessToOperateException;

import java.util.UUID;

/**
 * Service interface for managing project workflows.
 *
 * @author Roman Pejs
 */
public interface IProjectWFService {

    /**
     * Uploads a translated file for the specified project.
     *
     * @param projectId the ID of the project
     * @param resHeader the resource header containing file details
     * @return the updated Project with the uploaded translated file
     */
    Project uploadTranslatedFile(UUID projectId, ResourceHeader resHeader) throws NoAccessToOperateException;

    /**
     * Closes the project with the given ID.
     *
     * @param projectId the ID of the project to close
     * @return the closed Project
     */
    Project closeProject(UUID projectId) throws NoAccessToOperateException;

    /**
     * Approves the project with the given ID.
     *
     * @param projectId the ID of the project to approve
     * @return the approved Project
     */
    Project approveProject(UUID projectId) throws NoAccessToOperateException;

    /**
     * Rejects the project with the given ID, providing feedback.
     *
     * @param projectId the ID of the project to reject
     * @param feedback  feedback for the rejection
     * @return the rejected Project
     */
    Project rejectProject(UUID projectId, String feedback) throws NoAccessToOperateException;


}
