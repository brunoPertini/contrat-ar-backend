package com.contractar.microserviciomailing;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MailConfig {

    private final Environment env;

    public MailConfig(Environment env) {
        this.env = env;
    }

    @Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(env.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port")));
        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.debug", "true");

        return mailSender;
    }
    
    @Bean
    RestTemplate httpClient() {
    	return new RestTemplate();
    }
}
