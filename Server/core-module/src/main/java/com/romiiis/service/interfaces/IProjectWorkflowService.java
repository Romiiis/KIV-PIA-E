package com.romiiis.service.interfaces;

import com.romiiis.configuration.ResourceHeader;

import java.io.File;
import java.util.UUID;

/**
 * Service interface for managing project workflows.
 *
 * @author Roman Pejs
 */
public interface IProjectWorkflowService {

    /**
     * Uploads the translated file to the storage.
     *
     * @param resHeader ResourceHeader containing file metadata and data
     */
    void uploadTranslatedFile(UUID projectId, ResourceHeader resHeader);


}
