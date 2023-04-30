package com.ultimate.email.service;

import com.ultimate.email.model.EmailRequestDTO;
import com.ultimate.email.model.RequestDTO;
import com.ultimate.email.model.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

    public ResponseDTO sendEmail(EmailRequestDTO emailRequestDTO);

    public String saveFile(MultipartFile file, int archiveType);

    public void authenticateService(RequestDTO req);

    //public String saveFile(String imageData);
    public ResponseDTO sendEmail(EmailRequestDTO emailRequestDTO, MultipartFile file);


}
