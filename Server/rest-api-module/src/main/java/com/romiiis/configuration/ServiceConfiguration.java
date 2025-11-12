package com.romiiis.configuration;

import com.romiiis.DefaultFileSystemServiceImpl;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.security.CallerContextProvider;
import com.romiiis.security.ExecutionContext;
import com.romiiis.service.DefaultJwtServiceImpl;
import com.romiiis.service.DefaultPasswordHasherImpl;
import com.romiiis.service.MailHogEmailService;
import com.romiiis.service.impl.*;
import com.romiiis.service.interfaces.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfiguration {

    @Bean
    public CallerContextProvider callerContextProvider() {
        return new ExecutionContext();
    }

    @Bean
    public IUserService userService(IUserRepository userRepository, CallerContextProvider callerContextProvider) {
        return new DefaultUserServiceImpl(userRepository, callerContextProvider);
    }

    @Bean
    public IAuthService authService(IUserService userService, IUserRepository userRepository, IJwtService jwtService, IPasswordHasher passwordHasher, CallerContextProvider callerContextProvider) {
        return new DefaultAuthServiceImpl(userService, userRepository, passwordHasher, callerContextProvider);
    }

    @Bean
    public IFeedbackService feedbackService(IFeedbackRepository feedbackRepository, IProjectService projectService, CallerContextProvider callerContextProvider) {
        return new DefaultFeedbackServiceImpl(feedbackRepository, projectService, callerContextProvider);
    }

    @Bean
    public IFileSystemService fileSystemService(@Value("${fs.root:./}") String rootDir) {
        return new DefaultFileSystemServiceImpl(rootDir);
    }

    @Bean
    public IProjectService projectService(IUserService userService, IProjectRepository projectRepository, IFileSystemService fsService, CallerContextProvider callerContextProvider, IFeedbackRepository feedbackRepository) {
        return new DefaultProjectServiceImpl(userService, projectRepository, feedbackRepository, fsService,  callerContextProvider);
    }

    @Bean
    public IProjectWFService projectWorkflowService(IFileSystemService fsService, IProjectService projectService, IFeedbackService feedbackService, CallerContextProvider callerContextProvider, IDomainEventPublisher domainEventPublisher) {
        return new DefaultProjectWFServiceImpl(fsService, projectService, feedbackService, callerContextProvider, domainEventPublisher);
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
    public IJwtService jwtService(JwtProperties props, IUserService userService, CallerContextProvider callerContextProvider) {
        return new DefaultJwtServiceImpl(props, userService, callerContextProvider);
    }

    @Bean
    public IMailService mailService(JavaMailSender mailSender) {
        return new MailHogEmailService(mailSender);
    }


}
