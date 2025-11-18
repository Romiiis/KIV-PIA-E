package com.romiiis.configuration;

import com.romiiis.infrastructure.file.FileSystemServiceImpl;
import com.romiiis.infrastructure.mail.EmailService;
import com.romiiis.infrastructure.security.JwtServiceImpl;
import com.romiiis.infrastructure.security.PasswordHasherImpl;
import com.romiiis.infrastructure.security.config.JwtProperties;
import com.romiiis.port.*;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.infrastructure.security.ExecutionContext;

import com.romiiis.service.impl.*;
import com.romiiis.service.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfiguration {

    @Bean
    public IExecutionContextProvider callerContextProvider() {
        return new ExecutionContext();
    }

    @Bean
    public IUserService userService(IUserRepository userRepository, IExecutionContextProvider callerContextProvider) {
        return new UserServiceImpl(userRepository, callerContextProvider);
    }

    @Bean
    public IAuthService authService(IUserService userService, IUserRepository userRepository, IJwtService jwtService, IPasswordHasher passwordHasher, IExecutionContextProvider callerContextProvider) {
        return new AuthServiceImpl(userService, userRepository, passwordHasher, callerContextProvider);
    }

    @Bean
    public IFeedbackService feedbackService(IFeedbackRepository feedbackRepository, IProjectService projectService, IExecutionContextProvider callerContextProvider) {
        return new FeedbackServiceImpl(feedbackRepository, projectService, callerContextProvider);
    }

    @Bean
    public IFileSystemService fileSystemService(@Value("${fs.root:./}") String rootDir) {
        return new FileSystemServiceImpl(rootDir);
    }

    @Bean
    public IProjectService projectService(IUserService userService, IProjectRepository projectRepository, IFileSystemService fsService, IExecutionContextProvider callerContextProvider, IFeedbackRepository feedbackRepository, IDomainEventPublisher domainEventPublisher) {
        return new ProjectServiceImpl(userService, projectRepository, feedbackRepository, fsService,  callerContextProvider, domainEventPublisher);
    }

    @Bean
    public IProjectWFService projectWorkflowService(IFileSystemService fsService, IProjectService projectService, IFeedbackService feedbackService, IExecutionContextProvider callerContextProvider, IDomainEventPublisher domainEventPublisher) {
        return new ProjectWFServiceImpl(fsService, projectService, feedbackService, callerContextProvider, domainEventPublisher);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public IPasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new PasswordHasherImpl(passwordEncoder);
    }

    @Bean
    public IJwtService jwtService(JwtProperties props, IUserService userService, IExecutionContextProvider callerContextProvider) {
        return new JwtServiceImpl(props, userService, callerContextProvider);
    }

    @Bean
    public IMailService mailService(JavaMailSender mailSender) {
        return new EmailService(mailSender);
    }


}
