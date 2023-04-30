/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ultimate.email.controller;

import com.ultimate.email.model.EmailRequestDTO;
import com.ultimate.email.model.ResponseDTO;
import com.ultimate.email.model.ResultDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.ultimate.email.service.EmailService;

/**
 *
 * @author JunaidKhan
 */
@RestController
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailService fileSytemStorage;

    @GetMapping("/hello")
    public String Greet() {
        return "Welcome in Our Application";
    }

    @PostMapping("/sendemail")
    public ResponseEntity<?> sendEmail(
            @RequestHeader(value = "to") String[] to,
            @RequestHeader(value = "from") String from,
            @RequestHeader(value = "subject") String subject,
            @RequestHeader(value = "message") String message,
            @RequestHeader(value = "Cc") String[] Cc,
            @RequestHeader(value = "Bcc") String[] Bcc,
            @RequestParam(name = "file", required = false) MultipartFile file) throws Exception {
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setTo(to);
        emailRequestDTO.setFrom(from);
        emailRequestDTO.setSubject(subject);
        emailRequestDTO.setMessage(message);
        if (Cc != null && Cc.length > 0) {
            emailRequestDTO.setCc(Cc);
        }
        if (Bcc != null && Bcc.length > 0) {
            emailRequestDTO.setBcc(Bcc);
        }
        ResponseDTO response = fileSytemStorage.sendEmail(emailRequestDTO, file);
        if (response.getResult().getErrNo() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sendEmailByApex")
    public ResponseEntity<?> sendEmail(
            @RequestHeader(value = "to") String[] to,
            @RequestHeader(value = "from") String from,
            @RequestHeader(value = "subject") String subject,
            @RequestHeader(value = "message") String message,
            @RequestHeader(value = "fileName") String fileName,
            @RequestHeader(value = "Cc") String[] Cc,
            @RequestHeader(value = "Bcc") String[] Bcc) throws Exception {
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setTo(to);
        emailRequestDTO.setFrom(from);
        emailRequestDTO.setSubject(subject);
        emailRequestDTO.setMessage(message);
        emailRequestDTO.setFileName(fileName);
        if (Cc != null && Cc.length > 0) {
            emailRequestDTO.setCc(Cc);
        }
        if (Bcc != null && Bcc.length > 0) {
            emailRequestDTO.setBcc(Bcc);
        }
        ResponseDTO response = fileSytemStorage.sendEmail(emailRequestDTO);
        if (response.getResult().getErrNo() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadfile")
    public ResponseEntity<?> uploadSingleFile(@RequestParam("file") MultipartFile file) {
        int archiveType = 0;
        ResponseDTO response = new ResponseDTO();
        String upfile = fileSytemStorage.saveFile(file, archiveType);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/download/")
                .path(upfile).toUriString();
        response.setResult(new ResultDTO(HttpStatus.OK.value(), "file uploaded successfully"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/uploadFileToArchive")
    public ResponseEntity<?> uploadFileToArchive(@RequestParam("file") MultipartFile file) {
        int archiveType = 1;
        ResponseDTO response = new ResponseDTO();
        String upfile = fileSytemStorage.saveFile(file, archiveType);
        if (upfile == null) {
            response.setResult(new ResultDTO(HttpStatus.OK.value(), "file uploaded successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/download/")
                    .path(upfile).toUriString();
            response.setResult(new ResultDTO(HttpStatus.OK.value(), "file uploaded successfully"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/uploadFiles")
    public ResponseEntity<?> uploadFileToArchive(@RequestParam("file") MultipartFile[] files) {
        int archiveType = 1;
        ResponseDTO response = new ResponseDTO();
        List<?> responses = Arrays.asList(files)
                .stream()
                .map(file -> {
                    String upfile = fileSytemStorage.saveFile(file, archiveType);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/download/")
                            .path(upfile)
                            .toUriString();
                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .collect(Collectors.toList());

        for (MultipartFile multipartFile : files) {
            System.out.println("File name:::" + multipartFile.getOriginalFilename());
            System.out.println("Content Type:::::" + multipartFile.getContentType());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
