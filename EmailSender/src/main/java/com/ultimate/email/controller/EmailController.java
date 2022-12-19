/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ultimate.email.controller;

import com.ultimate.email.model.EmailRequestDTO;
import com.ultimate.email.service.IFileSytemStorage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author JunaidKhan
 */
@RestController
@CrossOrigin
public class EmailController {

    @Autowired
    IFileSytemStorage fileSytemStorage;

    @GetMapping("/hello")
    public String Greet() {
        return "Welcome in Our Application";
    }

    @PostMapping("/sendemail")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) throws Exception {
        System.out.println(emailRequestDTO);
        fileSytemStorage.sendEmail(emailRequestDTO);
        return new ResponseEntity<>(emailRequestDTO, HttpStatus.OK);
    }

    @PostMapping("/uploadfile")
    public ResponseEntity<?> uploadSingleFile(@RequestParam("file") MultipartFile file) {
        String upfile = fileSytemStorage.saveFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/download/")
                .path(upfile)
                .toUriString();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/uploadfiles")
    public ResponseEntity<List<?>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<?> responses = Arrays.asList(files)
                .stream()
                .map(file -> {
                    String upfile = fileSytemStorage.saveFile(file);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/download/")
                            .path(upfile)
                            .toUriString();
                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
