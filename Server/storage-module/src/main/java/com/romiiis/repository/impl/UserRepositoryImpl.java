package com.romiiis.repository.impl;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.mapper.MongoUserMapper;
import com.romiiis.repository.IUserRepository;
import com.romiiis.repository.mongo.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    private final MongoUserRepository mongoRepo;
    private final MongoUserMapper mapper;


    @Override
    public User getUserById(UUID id) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public void saveUser(User user) {
        mongoRepo.save(mapper.mapDomainToDB(user));

    }

    @Override
    public UserRole getRoleById(UUID id) {
        return null;
    }
}
