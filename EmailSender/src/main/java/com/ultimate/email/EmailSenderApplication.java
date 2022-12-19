package com.ultimate.email;

import com.ultimate.email.properties.FileUploadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileUploadProperties.class})
public class EmailSenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailSenderApplication.class, args);
        
    }

}
