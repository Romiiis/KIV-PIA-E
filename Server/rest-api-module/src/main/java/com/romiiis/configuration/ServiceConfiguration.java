package com.romiiis.configuration;

import com.romiiis.DefaultFileSystemServiceImpl;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.DefaultJwtServiceImpl;
import com.romiiis.service.DefaultPasswordHasherImpl;
import com.romiiis.service.impl.*;
import com.romiiis.service.interfaces.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfiguration {

    @Bean
    public IUserService userService(IUserRepository userRepository) {
        return new DefaultUserServiceImpl(userRepository);
    }

    @Bean
    public IAuthService authService(IUserService userService, IUserRepository userRepository, IJwtService jwtService, IPasswordHasher passwordHasher) {
        return new DefaultAuthServiceImpl(userService, userRepository, jwtService, passwordHasher);
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

    @Bean
    public IProjectWorkflowService projectWorkflowService(IFileSystemService fsService, IProjectService projectService) {
        return new DefaultProjectWorkflowServiceImpl(fsService, projectService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public IPasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new DefaultPasswordHasherImpl(passwordEncoder);
    }

    @Bean
    public IJwtService jwtService(JwtProperties props) {
        return new DefaultJwtServiceImpl(props);
    }


}
