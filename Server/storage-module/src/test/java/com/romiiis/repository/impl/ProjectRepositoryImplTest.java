package com.romiiis.repository.impl;

import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.mapper.MongoProjectMapper;
import com.romiiis.model.ProjectDB;
import com.romiiis.model.UserDB;
import com.romiiis.model.UserRoleDB;
import com.romiiis.repository.mongo.MongoProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataMongoTest(
        excludeAutoConfiguration = MongoRepositoriesAutoConfiguration.class
)@Import({ProjectRepositoryImpl.class, ProjectRepositoryImplTest.IntegrationConfig.class})
class ProjectRepositoryImplTest {

    @Autowired
    private ProjectRepositoryImpl projectRepository;

    @Autowired @Lazy
    private MongoProjectRepository mongoRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoProjectMapper mapper;

    private UUID translatorId;
    private UserDB translatorDB;
    private UserDB customerDB;
    private Project projectDomain;


    @Configuration
    @Lazy
    @EnableMongoRepositories(basePackages = "com.romiiis.repository.mongo")
    static class IntegrationConfig {

        @Bean
        MongoProjectMapper mongoProjectMapper() {
            return Mockito.mock(MongoProjectMapper.class);
        }
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();

        UUID customerId = UUID.randomUUID();
        translatorId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        customerDB = new UserDB();
        customerDB.setId(customerId);
        customerDB.setRole(UserRoleDB.CUSTOMER);
        mongoTemplate.save(customerDB);

        translatorDB = new UserDB();
        translatorDB.setId(translatorId);
        translatorDB.setRole(UserRoleDB.TRANSLATOR);
        mongoTemplate.save(translatorDB);

        User customerDomain = new User(customerId);
        User translatorDomain = new User(translatorId);

        projectDomain = Project.builder()
                .id(projectId)
                .customer(customerDomain)
                .translator(translatorDomain)
                .targetLanguage(Locale.GERMAN)
                .build();
    }

    @DisplayName("save() should persist Project to MongoDB")
    @Test
    void save_shouldPersistProjectToMongo() {
        ProjectDB projectDB = new ProjectDB();
        projectDB.setId(projectDomain.getId());
        projectDB.setTranslator(translatorDB);
        when(mapper.mapDomainToDB(any(Project.class))).thenReturn(projectDB);

        projectRepository.save(projectDomain);

        ProjectDB savedEntity = mongoRepo.findById(projectDomain.getId()).orElse(null);
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getTranslator().getId()).isEqualTo(translatorId);
    }

    @DisplayName("findById() should return Domain Project when exists")
    @Test
    void findById_shouldReturnDomainProject_whenExists() {
        ProjectDB projectDB = new ProjectDB();
        projectDB.setId(projectDomain.getId());
        projectDB.setCustomer(customerDB);
        projectDB.setTranslator(translatorDB);
        projectDB.setTargetLanguage(Locale.ITALIAN);
        mongoRepo.save(projectDB);

        when(mapper.mapDBToDomain(any(ProjectDB.class))).thenReturn(projectDomain);

        Project result = projectRepository.findById(projectDomain.getId());

        assertThat(result).isEqualTo(projectDomain);
    }

    @DisplayName("countProjectsWithTranslator() should return correct count")
    @Test
    void countProjectsWithTranslator_shouldReturnCorrectCount() {
        ProjectDB p1 = new ProjectDB();
        p1.setId(UUID.randomUUID());
        p1.setTranslator(translatorDB);
        mongoRepo.save(p1);

        UserDB otherTranslator = new UserDB();
        otherTranslator.setId(UUID.randomUUID());
        otherTranslator.setRole(UserRoleDB.TRANSLATOR);
        mongoTemplate.save(otherTranslator);

        ProjectDB p2 = new ProjectDB();
        p2.setId(UUID.randomUUID());
        p2.setTranslator(otherTranslator);
        mongoRepo.save(p2);

        int count = projectRepository.countProjectsWithTranslator(translatorId);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("getAllProjectIdsAsString() should return all project IDs as strings")
    @Test
    void getAllProjectIdsAsString_shouldReturnAllIds() {
        ProjectDB p1 = new ProjectDB(); p1.setId(UUID.randomUUID());
        ProjectDB p2 = new ProjectDB(); p2.setId(UUID.randomUUID());
        mongoRepo.saveAll(List.of(p1, p2));

        List<String> ids = projectRepository.getAllProjectIdsAsString();

        assertThat(ids).hasSize(2);
        assertThat(ids).containsExactlyInAnyOrder(p1.getId().toString(), p2.getId().toString());
    }

    @DisplayName("deleteAll() should clear all projects")
    @Test
    void deleteAll_shouldClearAllProjects() {
        ProjectDB p1 = new ProjectDB(); p1.setId(UUID.randomUUID());
        mongoRepo.save(p1);
        assertThat(mongoRepo.count()).isEqualTo(1);

        projectRepository.deleteAll();

        assertThat(mongoRepo.count()).isEqualTo(0);
    }
}