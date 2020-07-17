package com.maindola.pdftools.prettypdf.controller;

import com.maindola.pdftools.prettypdf.exceptions.InternalException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPDFException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPageNumbersException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPassword;
import com.maindola.pdftools.prettypdf.service.RestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    /**
     * Return the PDF Response from the File Path
     *
     * @param inputFilePath File Path
     * @return Response Entity having PDF File
     */
    private ResponseEntity<Resource> getFileResponse(Path inputFilePath, String type) {
        // Form Response Header
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + inputFilePath.getFileName()+"\"");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        // Get the Response from API
        String mediaType = type
                .equals("pdf") ? MediaType.APPLICATION_PDF_VALUE : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        System.out.println("output file = " + inputFilePath.getFileName());
        try {
            Resource resource = new ByteArrayResource(Files.readAllBytes(inputFilePath));
            ResponseEntity<Resource> response = ResponseEntity.ok()
                    .headers(header)
                    .contentLength(inputFilePath.toFile().length())
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            throw new InternalException();
        } finally {
            logger.info(inputFilePath.toAbsolutePath().toString() + " : Deleting the File on exit");
            try {
                FileUtils.forceDelete(inputFilePath.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        return new ResponseEntity<>(null, header, HttpStatus.NOT_FOUND);
    }

    /**
     * REST API to accept a PDF file and encrypt it, if already encrypted then password will be changed
     *
     * @param pdfFile  PDF File to be encrypted
     * @param password (New) Password of PDF file
     * @return New PDF file
     */
    @PostMapping(value = "/encrypt", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public ResponseEntity<Resource> encryptPDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                                               @RequestParam(name = "password") String password,
                                               @RequestParam(name = "pdfPassword", required = false) String pdfPassword

    ) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        // Check if password is not empty
        if (isEmpty(password)) {
            throw new InvalidPassword("Please enter a Password for PDF File.");
        } else if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }

        Path pdfFilePath = pdfService.encryptPDF(pdfFile, password, pdfPassword);
        return getFileResponse(pdfFilePath, "pdf");
    }

    /**
     * REST API to accept encrypted PDF file and decrypt it
     *
     * @param pdfFile  PDF File
     * @param password Password of PDF file
     * @return New PDF file
     */
    @PostMapping(value = "/decrypt", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public ResponseEntity<Resource> decryptPDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                                               @RequestParam(name = "password") String password) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        // Check if password is not empty
        if (isEmpty(password)) {
            throw new InvalidPassword("Please enter a Password for PDF File.");
        } else if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }

        Path pdfFilePath = pdfService.decryptPDF(pdfFile, password);
        return getFileResponse(pdfFilePath, "pdf");
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

    /**
     * REST API to accept a PDF file and split it into multiple PDF files based on mentioned page numbers
     * For e.g. "2,7,10" value as page number will create 4 PDF files
     * 1. Page Range 1-2
     * 2. Page Range 3-7
     * 3. Page Range 8-10
     * 4. Page Range 10-{LAST_PAGE}
     *
     * @param pdfFile     PDF File
     * @param pageNumbers page Numbers
     * @param pdfPassword Password of PDF file, if encrypted
     * @return
     */
    @PostMapping(value = "/split", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public ResponseEntity<Resource> splitPDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                                             @RequestParam(name = "pageNumbers") String pageNumbers,
                                             @RequestParam(name = "pdfPassword", required = false) String pdfPassword) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        // Check if pageNumbers is not empty
        if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }
        // Check and get Valid Page Numbers
        List<Integer> pageNumbersList = getPageNumbersList(pageNumbers);
        pageNumbersList.add(-1); // Add last entry to split file from last split point to end of document
        Path zipFilePath = pdfService.splitPDF(pdfFile, pageNumbersList, pdfPassword);
        return getFileResponse(zipFilePath, "octet");
    }

    /**
     * REST API to accept a PDF file and delete single/multiple pages from input PDF file based on mentioned page
     * numbers
     * For e.g. "2,7,10" value as page numbers will delete page 2, 7 and 10
     *
     * @param pdfFile     PDF File
     * @param pageNumbers page Numbers
     * @param pdfPassword Password of PDF file, if encrypted
     * @return
     */
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public ResponseEntity<Resource> deletePDF(@RequestParam(name = "pdfFile") MultipartFile pdfFile,
                                              @RequestParam(name = "pageNumbers") String pageNumbers,
                                              @RequestParam(name = "pdfPassword", required = false) String pdfPassword) {
        String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
        // Check if pageNumbers is not empty
        if (fileName == null || fileName.length() == 0) {
            throw new InvalidPDFException("Please enter a PDF File.");
        }
        // Check and get Valid Page Numbers
        List<Integer> pageNumbersList = getPageNumbersList(pageNumbers);
        Path pdfFilePath = pdfService.deletePDF(pdfFile, pageNumbersList, pdfPassword);
        return getFileResponse(pdfFilePath, "pdf");
    }

    /**
     * REST API to accept multiple PDF files and merge them into a single PDF file
     *
     * @param pdfFiles    Multiple PDF Files to be combined
     * @param pdfPassword Password of PDF file, if encrypted
     * @return merged PDF file
     */
    @PostMapping(value = "/merge", produces = MediaType.APPLICATION_PDF_VALUE, consumes = {"multipart/form-data"})
    public ResponseEntity<Resource> mergePDF(@RequestParam(name = "pdfFiles") MultipartFile[] pdfFiles,
                                             @RequestParam(name = "pdfPassword", required = false) String pdfPassword) {
        for (MultipartFile pdfFile : pdfFiles) {
            String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
            // Check if pageNumbers is not empty
            if (fileName == null || fileName.length() == 0) {
                throw new InvalidPDFException("Please enter a PDF File.");
            }
        }

        // Check and get Valid Page Numbers
        Path pdfFilePath = pdfService.mergePDF(pdfFiles, pdfPassword);
        return getFileResponse(pdfFilePath, "pdf");
    }
}
