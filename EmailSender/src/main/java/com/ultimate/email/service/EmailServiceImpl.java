/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ultimate.email.service;

import com.ultimate.email.exception.FileStorageException;
import com.ultimate.email.model.EmailRequestDTO;
import com.ultimate.email.model.RequestDTO;
import com.ultimate.email.model.ResponseDTO;
import com.ultimate.email.model.ResultDTO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author JunaidKhan
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public ResponseDTO sendEmail(EmailRequestDTO emailRequestDTO) {
        ResponseDTO response = new ResponseDTO();
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
                return new PasswordAuthentication("write your mail id", "write app password of 16 digit");
            }
        });
        session.setDebug(true);
        try {
            //Step 2 : compose the message [text,multi media]
            MimeMessage msg = new MimeMessage(session);
            //from email
            msg.setFrom(new InternetAddress(emailRequestDTO.getFrom()));
            //adding recipient to message
            String[] listOfTo = emailRequestDTO.getTo();
            for (int i = 0; i < listOfTo.length; i++) {
                System.out.println(listOfTo[i]);
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(listOfTo[i]));
            }

            //adding Cc to message
            String[] listofCcIDS = emailRequestDTO.getCc();
            if (emailRequestDTO.getCc() != null) {
                for (String Cc : listofCcIDS) {
                    msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(Cc));
                }
            }
            //adding Bcc to message
            String[] listofBccIDS = emailRequestDTO.getBcc();
            if (emailRequestDTO.getBcc() != null) {
                for (String Bcc : listofBccIDS) {
                    msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(Bcc));
                }
            }
            //adding subject to message
            msg.setSubject(emailRequestDTO.getSubject());
            //adding message to message
            msg.setText(emailRequestDTO.getMessage());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            BodyPart msgBodyPart = new MimeBodyPart();
            msgBodyPart.setContent(emailRequestDTO.getMessage(), "text/html");

            if (emailRequestDTO.getFileName().substring(0).length() > 0 && emailRequestDTO.getFileName() != null) {
                String wrkingDir = System.getProperty("user.dir") + File.separator + "uploaded_files";
                String attachmentName = emailRequestDTO.getFileName().replace("C:\\fakepath\\", "");
                File att = new File(new File(wrkingDir), attachmentName);
                messageBodyPart.attachFile(att);

                DataSource source = new FileDataSource(att);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attachmentName);
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(msgBodyPart);
                msg.setContent(multipart);
                Transport.send(msg);
                System.out.println("Message Sending successful with attachment");
            } else {
                msg.setContent(emailRequestDTO.getMessage(), "text/html");
                Transport.send(msg);
                System.out.println("Message Sending successful");
            }

            String uploadedFilePath = System.getProperty("user.dir") + File.separator + "uploaded_files";
            File uploadedFile = new File(uploadedFilePath + File.separator + emailRequestDTO.getFileName());
            if (uploadedFile.isFile()) {
                uploadedFile.delete();
            }
            response.setResult(new ResultDTO(HttpStatus.OK.value(), "Message Sending successful"));

        } catch (IOException | MessagingException mex) {
            System.out.println("Message Sending Failed" + mex);
            mex.printStackTrace();
            response.setResult(new ResultDTO(HttpStatus.BAD_REQUEST.value(), "Message Sending Failed"));
        }
        return response;
    }

    @Override
    public String saveFile(MultipartFile file, int archiveType) {
        String fileName = null;
        BufferedReader br;
        FileOutputStream fos = null;
        try {
            fileName = file.getOriginalFilename();
            String base64Encode = "";
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                //  System.out.println(line);
                base64Encode += line;
            }
            String path = null;
            if (archiveType == 0) {
                path = System.getProperty("user.dir") + File.separator + "uploaded_files";
            } else if (archiveType == 1) {
                path = System.getProperty("user.dir") + File.separator + "archived_files";
            }

            File fileSaveDir = new File(path);

            // Creates the save directory if it does not exists
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            byte[] fileByte = java.util.Base64.getDecoder().decode(base64Encode);
            // write the image to a file
            File outputfile = new File(path + File.separator + fileName);
            outputfile.createNewFile();
            // ImageIO.write(image, "txt", outputfile);
            fos = new FileOutputStream(outputfile);
            fos.write(fileByte);

        } catch (IOException e) {
            //throw new FileStorageException("Could not upload file");
            e.printStackTrace();
            fileName = null;
        } catch (Exception e) {
            //throw new FileStorageException("Could not upload file");
            e.printStackTrace();
            fileName = null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return fileName;
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

    @Override
    public ResponseDTO sendEmail(EmailRequestDTO emailRequestDTO, MultipartFile file) {
        ResponseDTO response = new ResponseDTO();
        String fileName = file.getOriginalFilename();//filename
        BufferedReader br;
        FileOutputStream fos = null;
        String host = "smtp.gmail.com";
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
                return new PasswordAuthentication //        ("jkhan18jan1998@gmail.com", "afwxfklbuysusxcq");
                        ("ultimatetek.in@gmail.com", "jpeakbcvivninjff");
            }
        });
        session.setDebug(true);

        try {
            //Step 2 : compose the message [text,multi media]
            MimeMessage msg = new MimeMessage(session);
            //from email
            msg.setFrom(new InternetAddress(emailRequestDTO.getFrom()));
            //adding subject to message
            msg.setSubject(emailRequestDTO.getSubject());
            //adding message to message
            msg.setText(emailRequestDTO.getMessage());
            //adding recipient to message
            String[] listOfTo = emailRequestDTO.getTo();
            for (int i = 0; i < listOfTo.length; i++) {
                System.out.println(listOfTo[i]);
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(listOfTo[i]));
            }

            //adding Cc to message
            String[] listofCcIDS = emailRequestDTO.getCc();
            if (emailRequestDTO.getCc() != null) {
                for (String cc : listofCcIDS) {
                    msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
                }
            }
            //adding Bcc to message
            String[] listofBccIDS = emailRequestDTO.getBcc();
            if (emailRequestDTO.getBcc() != null) {
                for (String Bcc : listofBccIDS) {
                    msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(Bcc));
                }
            }

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            BodyPart msgBodyPart = new MimeBodyPart();
            msgBodyPart.setContent(emailRequestDTO.getMessage(), "text/html");
            //upload and send attachment
            if (!file.isEmpty() && file.getOriginalFilename() != null) {
                try {
                    //init();//first check if file is deleted then creat
                    fileName = file.getOriginalFilename();
                    String base64Encode = "";
                    String line;
                    InputStream is = file.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        //  System.out.println(line);
                        base64Encode += line;
                    }
                    String path = null;
                    path = System.getProperty("user.dir") + File.separator + "uploaded_files";

                    File fileSaveDir = new File(path);

                    // Creates the save directory if it does not exists
                    if (!fileSaveDir.exists()) {
                        fileSaveDir.mkdirs();
                    }
                    byte[] fileByte = java.util.Base64.getDecoder().decode(base64Encode);
                    File outputfile = new File(path + File.separator + fileName);
                    outputfile.createNewFile();
                    // ImageIO.write(image, "txt", outputfile);
                    fos = new FileOutputStream(outputfile);
                    fos.write(fileByte);

                    messageBodyPart.attachFile(outputfile);
                    DataSource source = new FileDataSource(outputfile);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(msgBodyPart);
                    multipart.addBodyPart(messageBodyPart);

                    msg.setContent(multipart);
                    Transport.send(msg);
                    System.out.println("Message Sending successful with attachment");

                } catch (IOException | MessagingException e) {
                    e.printStackTrace();
                    throw new FileStorageException("Could not upload file");
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            } else {
                msg.setContent(emailRequestDTO.getMessage(), "text/html");
                System.out.println("Massage ::::::::::;" + msg.getMessageID());
                Transport.send(msg);
                System.out.println("Message Sending successful");
            }
            response.setResult(new ResultDTO(HttpStatus.OK.value(), "Message Sending successful"));
        } catch (MessagingException mex) {
            mex.printStackTrace();
            response.setResult(new ResultDTO(HttpStatus.BAD_REQUEST.value(), "Message Sending Failed"));
        }
        return response;
    }

}
