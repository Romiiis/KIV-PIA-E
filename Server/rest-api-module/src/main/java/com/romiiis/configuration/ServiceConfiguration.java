package com.romiiis.configuration;

import com.romiiis.DefaultFileSystemServiceImpl;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.impl.*;
import com.romiiis.service.interfaces.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public IUserService userService(IUserRepository userRepository) {
        return new DefaultUserServiceImpl(userRepository);
    }

    @Bean
    public IAuthService authService(IUserService userService, IUserRepository userRepository, IJwtService jwtService) {
        return new DefaultAuthServiceImpl(userService, userRepository, jwtService);
    }

    @Bean
    public IFeedbackService feedbackService(IFeedbackRepository feedbackRepository) {
        return new DefaultFeedbackServiceImpl(feedbackRepository);
    }

    @Bean
    public IFileSystemService fileSystemService() {
        return new DefaultFileSystemServiceImpl();
    }

    @Bean
    public IProjectService projectService(IUserService userService, IProjectRepository projectRepository, IFileSystemService fsService) {
        return new DefaultProjectServiceImpl(userService, projectRepository, fsService);
    }




}
