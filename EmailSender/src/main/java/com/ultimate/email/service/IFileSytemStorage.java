package com.ultimate.email.service;

import com.ultimate.email.model.EmailRequestDTO;
import com.ultimate.email.model.RequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IFileSytemStorage {

    void init();

    void sendEmail(EmailRequestDTO emailRequestDTO);

    String saveFile(MultipartFile file);

    public void authenticateService(RequestDTO req);

}
