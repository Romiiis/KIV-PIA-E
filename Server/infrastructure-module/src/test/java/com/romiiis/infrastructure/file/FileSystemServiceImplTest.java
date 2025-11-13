package com.romiiis.infrastructure.file;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.exception.FileNotFoundException;
import org.junit.jupiter.api.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

/**
 */
class FileSystemServiceImplTest {

    private FileSystemServiceImpl fileSystemService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary directory for test isolation
        var tempDirName = "tempTestDir";

        // Create anonymous subclass to override rootDirectory
        fileSystemService = new FileSystemServiceImpl(tempDirName);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary directory after each test
        var rootDir = Path.of("tempTestDir");
        if (Files.exists(rootDir)) {
            Files.walk(rootDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore
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

        Assertions.assertNotNull(result);
        Assertions.assertEquals("original", result.resourceName());
        Assertions.assertArrayEquals(content, result.resourceData());
    }

    @DisplayName("saveTranslatedFile and getTranslatedFile should store and retrieve file correctly")
    @Test
    void saveAndGetTranslatedFile_success() throws Exception {
        UUID projectId = UUID.randomUUID();
        byte[] content = "Translated text".getBytes();

        fileSystemService.saveTranslatedFile(projectId, content);
        ResourceHeader result = fileSystemService.getTranslatedFile(projectId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("translated", result.resourceName());
        Assertions.assertArrayEquals(content, result.resourceData());
    }

    @DisplayName("getOriginalFile should throw FileNotFoundException if file does not exist")
    @Test
    void getOriginalFile_shouldThrow_whenMissing() {
        UUID projectId = UUID.randomUUID();
        Assertions.assertThrows(FileNotFoundException.class, () -> fileSystemService.getOriginalFile(projectId));
    }

    @DisplayName("getTranslatedFile should throw FileNotFoundException if file does not exist")
    @Test
    void getTranslatedFile_shouldThrow_whenMissing() {
        UUID projectId = UUID.randomUUID();
        Assertions.assertThrows(FileNotFoundException.class, () -> fileSystemService.getTranslatedFile(projectId));
    }
}
