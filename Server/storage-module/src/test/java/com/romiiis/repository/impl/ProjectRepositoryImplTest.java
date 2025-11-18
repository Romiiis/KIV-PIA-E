package com.romiiis.repository.impl;

import com.romiiis.domain.Project;
import com.romiiis.mapper.MongoProjectMapper;
import com.romiiis.repository.mongo.MongoProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@EnableMongoRepositories(basePackageClasses = MongoProjectRepository.class)
@Testcontainers
@Import({ProjectRepositoryImpl.class, MongoProjectMapper.class, MongoProjectRepository.class}) // Důležité: Importujeme impl a mapper, protože @DataMongoTest skenuje jen repository
class ProjectRepositoryImplTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProjectRepositoryImpl projectRepository;

    @Autowired
    private MongoProjectRepository mongoProjectRepository;

    @BeforeEach
    void setUp() {
        mongoProjectRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mongoProjectRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindProjectById() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        Project newProject = createDummyProject(projectId);

        // Act
        projectRepository.save(newProject);
        Project foundProject = projectRepository.findById(projectId);

        // Assert
        assertThat(foundProject).isNotNull();
        assertThat(foundProject.getId()).isEqualTo(projectId);
        // Zde můžeš přidat další asserty pro kontrolu mapování polí
    }

    @Test
    void shouldCountProjectsWithTranslator() {
        // Tento test ověřuje tvou query: "{ 'translator.$id': ?0 }"

        // Arrange
        UUID translatorId1 = UUID.randomUUID();
        UUID translatorId2 = UUID.randomUUID();

        Project project1 = createDummyProject(UUID.randomUUID());
        setTranslatorId(project1, translatorId1); // Předpokládaná metoda na doménovém objektu

        Project project2 = createDummyProject(UUID.randomUUID());
        setTranslatorId(project2, translatorId1);

        Project project3 = createDummyProject(UUID.randomUUID());
        setTranslatorId(project3, translatorId2); // Jiný překladatel

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        // Act
        int count = projectRepository.countProjectsWithTranslator(translatorId1);

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldGetAllProjectIdsAsString() {
        // Tento test ověřuje query: @Query(value = "{}", fields = "{ '_id' : 1 }")

        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        projectRepository.save(createDummyProject(id1));
        projectRepository.save(createDummyProject(id2));

        // Act
        List<String> ids = projectRepository.getAllProjectIdsAsString();

        // Assert
        assertThat(ids).hasSize(2);
        assertThat(ids).containsExactlyInAnyOrder(id1.toString(), id2.toString());
    }

    // --- Pomocné metody pro vytváření dat (uprav dle své třídy Project) ---

    private Project createDummyProject(UUID id) {
        return Project.builder().id(id)
    }

    private void setTranslatorId(Project project, UUID translatorId) {
        // Předpokládaná metoda pro nastavení ID překladatele v objektu Project
        // Uprav dle své implementace
        // Například:

    }
}