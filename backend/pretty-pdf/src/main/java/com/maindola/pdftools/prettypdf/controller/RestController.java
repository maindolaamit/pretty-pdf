package com.maindola.pdftools.prettypdf.controller;

import com.maindola.pdftools.prettypdf.exceptions.InvalidPDFException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPageNumbersException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPassword;
import com.maindola.pdftools.prettypdf.service.RestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.maindola.pdftools.prettypdf.service.Utility.isEmpty;

@org.springframework.web.bind.annotation.RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequestMapping("/pretty-pdf")
public class RestController {
    private static final Logger logger = LogManager.getLogger(RestService.class);

    @Autowired
    RestService pdfService;

    @PostMapping(value = "/encrypt", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public String encryptPDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "pdfPassword", required = false) String pdfPassword

    ) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        logger.info(fileName);
        // Check if password is not empty
        if (isEmpty(password)) {
            throw new InvalidPassword("Please enter a Password for PDF File.");
        } else if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }

        pdfService.encryptPDF(pdfFile, password, pdfPassword);
//        return new ResponseEntity<>(null, header, HttpStatus.NOT_FOUND);
        return "success";
    }

    @PostMapping(value = "/decrypt", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public String decryptPDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                             @RequestParam(name = "password") String password) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        logger.info(fileName);
        // Check if password is not empty
        if (isEmpty(password)) {
            throw new InvalidPassword("Please enter a Password for PDF File.");
        } else if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }

        pdfService.decryptPDF(pdfFile, password);
//        return new ResponseEntity<>(null, header, HttpStatus.NOT_FOUND);
        return "success";
    }

    private List<Integer> getPageNumbersList(String pageNumbers) {
        if (isEmpty(pageNumbers)) {
            throw new InvalidPageNumbersException("Please enter Page numbers, e.g. 1,2,3.");
        }

        // Check if pages numbers are valid
        List<Integer> pageNumbersList;
        try {
            pageNumbersList = Arrays.stream(pageNumbers.split(",")).map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InvalidPageNumbersException("Page number should have only numbers separated comma if multiple" +
                    " pages.");
        }

        // Loop to check if any negative page number is sent
        for (Integer integer : pageNumbersList) {
            if (integer.intValue() <= 0) {
                throw new InvalidPageNumbersException("Page number should be greater than 0.");
            }
        }
        return pageNumbersList;

    }

    @PostMapping(value = "/split", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public String splitPDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                           @RequestParam(name = "pageNumbers") String pageNumbers,
                           @RequestParam(name = "pdfPassword", required = false) String pdfPassword) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        logger.info(fileName);
        // Check if pageNumbers is not empty
        if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }
        // Check and get Valid Page Numbers
        List<Integer> pageNumbersList = getPageNumbersList(pageNumbers);
        pageNumbersList.add(-1); // Add last entry to split file from last split point to end of document

        pdfService.splitPDF(pdfFile, pageNumbersList, pdfPassword);

//        return new ResponseEntity<>(null, header, HttpStatus.NOT_FOUND);
        return "success";
    }

    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public String deletePDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                            @RequestParam(name = "pageNumbers") String pageNumbers,
                            @RequestParam(name = "pdfPassword", required = false) String pdfPassword) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        logger.info(fileName);
        // Check if pageNumbers is not empty
        if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }
        // Check and get Valid Page Numbers
        List<Integer> pageNumbersList = getPageNumbersList(pageNumbers);
        pdfService.deletePDF(pdfFile, pageNumbersList, pdfPassword);

//        return new ResponseEntity<>(null, header, HttpStatus.NOT_FOUND);
        return "success";
    }

    @PostMapping(value = "/merge", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public String mergePDF(@RequestParam(name = "pdfFiles") MultipartFile[] pdfFiles,
                           @RequestParam(name = "pdfPassword", required = false) String pdfPassword) {
        for (MultipartFile pdfFile : pdfFiles) {
            String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
            logger.info(fileName);
            // Check if pageNumbers is not empty
            if (fileName == null || fileName.length() == 0) {
                throw new InvalidPDFException("Please enter a PDF File.");
            }
        }

        // Check and get Valid Page Numbers
        pdfService.mergePDF(pdfFiles, pdfPassword);

//        return new ResponseEntity<>(null, header, HttpStatus.NOT_FOUND);
        return "success";
    }

}
