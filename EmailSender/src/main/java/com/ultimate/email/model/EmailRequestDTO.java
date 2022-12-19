package com.ultimate.email.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    public String to;
    public String from;
    public String subject;
    public String message;
    public String nameoffile;

    public EmailRequestDTO(String nameoffile) {
        this.nameoffile = nameoffile;
    }

}
