/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ultimate.email.service;

import com.ultimate.email.exception.FileStorageException;
import com.ultimate.email.model.EmailRequestDTO;
import com.ultimate.email.model.RequestDTO;
import com.ultimate.email.properties.FileUploadProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author JunaidKhan
 */
@Service
public class EmailService implements IFileSytemStorage {

    private final Path dirLocation;

    @Autowired
    public EmailService(FileUploadProperties fileUploadProperties) {
        this.dirLocation = Paths.get(fileUploadProperties.getLocation()).toAbsolutePath().normalize();
    }

    @Override
    @PostConstruct
    public void init() {
        // TODO Auto-generated method stub
        try {
            boolean isDir = Files.isDirectory(this.dirLocation);
            if (!isDir) {
                Files.createDirectories(this.dirLocation);
            } else {
                System.out.println("diroctory is already Created");
            }
        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload dir!");
        }
    }

    @Override
    public String saveFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            Path dfile = this.dirLocation.resolve(fileName);
            System.out.println("fileName :" + fileName);
            Files.copy(file.getInputStream(), dfile, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (Exception e) {
            throw new FileStorageException("Could not upload file");
        }

    }

    @Override
    public void sendEmail(EmailRequestDTO emailRequestDTO) {
        String host = "smtp.gmail.com";
        RequestDTO req = new RequestDTO();
        //get the system properties
        Properties props = new Properties();
        //setting important information to properties object
        //host set
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");
        System.out.println("Properties" + props);
        //Step 1: to get the session object..
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jkhan18jan1998@gmail.com", "afwxfklbuysusxcq");
            }
        });
        session.setDebug(true);
        try {
            //Step 2 : compose the message [text,multi media]
            MimeMessage msg = new MimeMessage(session);
            //from email
            msg.setFrom(new InternetAddress(emailRequestDTO.getFrom()));
            //adding recipient to message
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRequestDTO.getTo()));
            //adding subject to message
            msg.setSubject(emailRequestDTO.getSubject());
            //adding message to message
            msg.setText(emailRequestDTO.getMessage());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart = new MimeBodyPart();

            if (emailRequestDTO.getNameoffile().substring(0).length() > 0) {
                String attachmentPath = this.dirLocation.toString();
                String attachmentName = emailRequestDTO.getNameoffile().replace("C:\\fakepath\\", "");

                File att = new File(new File(attachmentPath), attachmentName);
                messageBodyPart.attachFile(att);

                DataSource source = new FileDataSource(att);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attachmentName);
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
                Transport.send(msg);
                System.out.println("Message Sending successful");
            } else {
                Transport.send(msg);
                System.out.println("Message Sending successful");
            }

        } catch (IOException | MessagingException mex) {
            System.out.println("Message Sending Failed" + mex);
            mex.printStackTrace();
        }
    }

    @Override
    public void authenticateService(RequestDTO req) {
        String username = req.getLoginData().getUsername();
        String password = req.getLoginData().getPassword();
//        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
    }

}
