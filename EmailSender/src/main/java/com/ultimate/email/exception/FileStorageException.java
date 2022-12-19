package com.ultimate.email.exception;

import lombok.Data;

@Data
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;
    private String message;

}
