package com.romiiis.repository.impl;

import com.romiiis.domain.Project;
import com.romiiis.mapper.MongoProjectMapper;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.mongo.MongoProjectRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    private final MongoProjectRepository mongoRepo;
    private final MongoProjectMapper mapper;

    public ProjectRepositoryImpl(MongoProjectRepository mongoRepo, MongoProjectMapper mapper) {
        this.mongoRepo = mongoRepo;
        this.mapper = mapper;
    }

    @Override
    public void store(Project project) {
        mongoRepo.save(mapper.mapDomainToDB(project));
    }

    @Override
    public List<Project> getAll() {
        return mongoRepo.findAll()
                .stream()
                .map(mapper::mapDBToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Project findById(UUID id) {
        return mongoRepo.findById(id)
                .map(mapper::mapDBToDomain)
                .orElse(null);
    }
}
