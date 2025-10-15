package com.romiiis.repository.impl;

import com.romiiis.domain.Project;
import com.romiiis.mapper.MongoProjectMapper;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.mongo.MongoProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the IProjectRepository interface using MongoDB as the data store.
 *
 * @author Roman Pejs
 */
@RequiredArgsConstructor
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    /**
     * MongoDB repository and mapper
     */
    private final MongoProjectRepository mongoRepo;
    private final MongoProjectMapper mapper;

    /**
     * Stores a project in the MongoDB database.
     *
     * @param project the project to be stored
     */
    @Override
    public void store(Project project) {
        mongoRepo.save(mapper.mapDomainToDB(project));
    }

    /**
     * Retrieves all projects from the MongoDB database.
     *
     * @return a list of all projects
     */
    @Override
    public List<Project> getAll() {
        return mongoRepo.findAll()
                .stream()
                .map(mapper::mapDBToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Finds a project by its unique identifier.
     *
     * @param id the unique identifier of the project
     * @return the project with the given id, or null if not found
     */
    @Override
    public Project findById(UUID id) {
        return mongoRepo.findById(id)
                .map(mapper::mapDBToDomain)
                .orElse(null);
    }
}
