package com.ultimate.email.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private List<String> erros;
    private ResultDTO result;

    public ResponseDTO(LocalDateTime timestamp, String message, List<String> erros) {
        this.timestamp = timestamp;
        this.message = message;
        this.erros = erros;
    }

    public ResponseDTO() {
    }

    public ResponseDTO(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

}
