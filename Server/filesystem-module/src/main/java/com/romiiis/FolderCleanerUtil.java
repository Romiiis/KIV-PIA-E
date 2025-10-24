package com.romiiis;

import com.romiiis.service.interfaces.IFileSystemService;
import com.romiiis.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class FolderCleanerUtil {

    private final IFileSystemService fsService;
    private final IProjectService projectService;

    @EventListener(ApplicationReadyEvent.class)
    public void cleanFolders() {

        log.info("Starting folder cleanup process...");
        List<String> allProjectIds = projectService.getAllProjectIdsAsString();
        List<String> existingFolders = fsService.listAllProjectFolders();

        // Convert lists to sets for easier comparison
        var projectIdSet = Set.copyOf(allProjectIds);
        var folderSet = Set.copyOf(existingFolders);

        AtomicInteger deletedCount = new AtomicInteger();

        // Identify folders that do not correspond to any existing project
        folderSet.stream()
                .filter(folder -> !projectIdSet.contains(folder))
                .forEach(folder -> {
                    fsService.deleteProjectFolder(folder);
                    deletedCount.getAndIncrement();
                    System.out.println("Deleted folder: " + folder);
                });

        log.info("Folder cleanup process completed. Total folders deleted: {}", deletedCount.get());

    }
}
