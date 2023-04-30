package com.ultimate.email.model;

import java.util.List;
import javax.mail.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    private String[] to;
    private String[] Cc;
    private String[] Bcc;
    private String from;
    private String subject;
    private String message;
    private String fileName;
    private String imageData;

    public EmailRequestDTO(String fileName) {
        this.fileName = fileName;
    }

}
