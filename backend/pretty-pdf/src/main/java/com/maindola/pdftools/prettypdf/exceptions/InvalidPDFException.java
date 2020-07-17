package com.maindola.pdftools.prettypdf.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidPDFException extends RuntimeException {
    public InvalidPDFException() {
        super("Invalid PDF file.");
    }

    public InvalidPDFException(String message) {
        super(message);

    }
}
