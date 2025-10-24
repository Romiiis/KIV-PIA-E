package com.romiiis;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.exception.FileNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-like unit tests for {@link DefaultFileSystemServiceImpl}.
 */
class DefaultFileSystemServiceImplTest {

    private DefaultFileSystemServiceImpl fileSystemService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary directory for test isolation
        tempDir = Files.createTempDirectory("test-filesystem-service");

        // Create anonymous subclass to override rootDirectory
        fileSystemService = new DefaultFileSystemServiceImpl();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder()) // delete children first
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @DisplayName("saveOriginalFile and getOriginalFile should store and retrieve file correctly")
    @Test
    void saveAndGetOriginalFile_success() throws Exception {
        UUID projectId = UUID.randomUUID();
        byte[] content = "Hello World!".getBytes();

        fileSystemService.saveOriginalFile(projectId, content);
        ResourceHeader result = fileSystemService.getOriginalFile(projectId);

        assertNotNull(result);
        assertEquals("original", result.resourceName());
        assertArrayEquals(content, result.resourceData());
    }

    @DisplayName("saveTranslatedFile and getTranslatedFile should store and retrieve file correctly")
    @Test
    void saveAndGetTranslatedFile_success() throws Exception {
        UUID projectId = UUID.randomUUID();
        byte[] content = "Translated text".getBytes();

        fileSystemService.saveTranslatedFile(projectId, content);
        ResourceHeader result = fileSystemService.getTranslatedFile(projectId);

        assertNotNull(result);
        assertEquals("translated", result.resourceName());
        assertArrayEquals(content, result.resourceData());
    }

    @DisplayName("getOriginalFile should throw FileNotFoundException if file does not exist")
    @Test
    void getOriginalFile_shouldThrow_whenMissing() {
        UUID projectId = UUID.randomUUID();
        assertThrows(FileNotFoundException.class, () -> fileSystemService.getOriginalFile(projectId));
    }

    @DisplayName("getTranslatedFile should throw FileNotFoundException if file does not exist")
    @Test
    void getTranslatedFile_shouldThrow_whenMissing() {
        UUID projectId = UUID.randomUUID();
        assertThrows(FileNotFoundException.class, () -> fileSystemService.getTranslatedFile(projectId));
    }
}
