package com.maindola.pdftools.prettypdf.service;

import com.maindola.pdftools.prettypdf.exceptions.InternalException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPDFException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.maindola.pdftools.prettypdf.service.Utility.*;

@Service
public class RestService {
    private final String appDir = "pretty-pdf";
    private final String userAppDir = System.getProperty("user.home").concat(File.separator).concat(appDir);
    private final Path workDir = Paths.get(userAppDir);
    private static final Logger logger = LogManager.getLogger(RestService.class);

    /**
     * Check if the Working directory is present or not
     * if not present will create a Working directory in the User Home
     */
    void checkWorkDir() {
        if (!Files.exists(workDir)) {
            // Create the directory
            try {
                Files.createDirectory(workDir);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to create working directory. " + e.getCause());
            }
        }
    }

    /**
     * Encrypt given PDF File with Password
     *
     * @param pdfFile     PDF File
     * @param password    Encryption key
     * @param pdfPassword Existing password if PDF file is already encrypted.
     */
    public void encryptPDF(MultipartFile pdfFile, String password, String pdfPassword) {
        // Check if working directory exists
        checkWorkDir();

        long startTime = System.currentTimeMillis();
        logger.info("Encrypting file.");
        // Load pdf
        PDDocument document = getPDDocuemnt(pdfFile, pdfPassword);

        try {
            // Create Access permission and Standard Protection policy
            AccessPermission permission = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, permission);
            // Set permission and Encryption key
            spp.setEncryptionKeyLength(128);
            spp.setPermissions(permission);
            document.protect(spp);
            document.setDocumentInformation(getDocumentInfo(document.getDocumentInformation()));
            // Set document information and save
            String encryptedFile = userAppDir.concat(File.separator).concat(pdfFile.getOriginalFilename());
            document.save(encryptedFile);
            logger.info("Encrypted File : " + encryptedFile, 1);
            logger.info(getElapsedTime(startTime));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }

    private PDDocument getPDDocuemnt(MultipartFile pdfFile, String pdfPassword) {
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile.getInputStream());
        } catch (InvalidPasswordException e) {
            if (isEmpty(pdfPassword)) {
                throw new InvalidPDFException("Please pass current password for PDF file in Request Parameter " +
                        "'pdfPassword' ");
            }
            try {
                logger.info("PDF File encrypted.");
                document = PDDocument.load(pdfFile.getInputStream(), pdfPassword);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                throw new InternalException(ioException.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
        return document;
    }

    /**
     * Decrypt a given PDF File by removing password with file
     *
     * @param pdfFile  PDF File
     * @param password Encryption key
     */
    public void decryptPDF(MultipartFile pdfFile, String password) {
        // Check if working directory exists
        checkWorkDir();
        long startTime = System.currentTimeMillis();

        // Load pdf
        try (PDDocument document = PDDocument.load(pdfFile.getInputStream(), password)) {
            String decryptedFile = userAppDir.concat(File.separator).concat(pdfFile.getOriginalFilename());
            if (document.isEncrypted()) {
                logger.info("Decrypting file.");
                document.setAllSecurityToBeRemoved(true);
                document.setDocumentInformation(getDocumentInfo(document.getDocumentInformation()));
                // Set document information and save
                document.save(decryptedFile);
                logger.info("Decrypted File : " + decryptedFile, 1);
                logger.info(getElapsedTime(startTime));
            } else {
                throw new InvalidPDFException("PDF file already decrypted");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }

    /**
     * Return autogenerated FileName to store Split PDF Files
     *
     * @param fileName PDF File name
     * @return Auto generated FileName
     */
    private String getAutoGeneratedSplitDir(String fileName) {
        Path splitPath =
                Paths.get(String.format("%s%s%s",
                        userAppDir,
                        File.separator,
                        fileName.replace(" ", "_").
                                replace(".pdf", "_split")
                ));
        if (!Files.exists(splitPath)) {
            // Create the directory
            try {
                Files.createDirectory(splitPath);
                System.out.println("Created Path" + splitPath);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to create working directory. " + e.getCause());
            }
        }

        return splitPath.toString();
    }

    /**
     * Split the input PDF file into multiple pages based on given Page Numbers list
     *
     * @param pdfFile     PDF File to split
     * @param pageNumbers Page Number range to split PDF File into
     * @param pdfPassword PDF File password if any, Optional
     */
    public void splitPDF(MultipartFile pdfFile, List<Integer> pageNumbers, String pdfPassword) {
        // Check if working directory exists
        checkWorkDir();
        String splitFilesDir = getAutoGeneratedSplitDir(pdfFile.getOriginalFilename());
        long startTime = System.currentTimeMillis();

        // Load pdf
        try (PDDocument document = getPDDocuemnt(pdfFile, pdfPassword)) {
            // Loop for the page numbers from which to split
            int documentCount = 0, startPageNumber = 1, endPageNumber = 0, lastPageNumber = document.getNumberOfPages();
            for (Integer splitPageNumber : pageNumbers) {
                // Check page number
                if (splitPageNumber > lastPageNumber) {
                    throw new InvalidPDFException(String.format("Split page number %d , Max number of page in PDF " +
                                    "file %d",
                            splitPageNumber, lastPageNumber));
                }

                // Determine end page number accordingly
                endPageNumber = (splitPageNumber.intValue() == -1) ? lastPageNumber : splitPageNumber.intValue();

                documentCount++; // Increase document count
                String fileName = splitFilesDir.concat(File.separator)
                        .concat(pdfFile.getOriginalFilename().replace(".pdf",
                                String.format("_%d.pdf", documentCount)));

                // Determine the page number range to split
                // Instantiate Splitter class
                Splitter splitter = new Splitter();
                splitter.setStartPage(startPageNumber);
                splitter.setEndPage(endPageNumber);
                splitter.setSplitAtPage(endPageNumber - startPageNumber + 1);
                System.out.printf("pages range %d-%d%n", startPageNumber, endPageNumber);

                //splitting the pages of a PDF document
                List<PDDocument> splitPDFs = splitter.split(document);
                for (PDDocument splitPDF : splitPDFs) {
                    splitPDF.save(fileName);
                    logger.info("Split file : " + fileName);
                    startPageNumber = endPageNumber + 1; // Change start page
                }
            }
            logger.info(getElapsedTime(startTime));
        } catch (
                IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }

    public void deletePDF(MultipartFile pdfFile, List<Integer> pageNumbersList, String pdfPassword) {
        // Check if working directory exists
        checkWorkDir();
        long startTime = System.currentTimeMillis();

        // Load pdf
        PDDocument document = getPDDocuemnt(pdfFile, pdfPassword);

        try {
            int lastPageNumber = document.getNumberOfPages();
            String modifiedFile = userAppDir.concat(File.separator).concat(pdfFile.getOriginalFilename());

            // Loop for each page to be deleted
            int adjustIndex = 1;
            for (Integer pageNumber : pageNumbersList) {
                // Check page number
                if (pageNumber > lastPageNumber) {
                    throw new InvalidPDFException(String.format("Split page number %d , Max number of page in PDF " +
                                    "file %d",
                            pageNumber, lastPageNumber));
                }
                //
                document.removePage(pageNumber - adjustIndex++);
                logger.info("Removed page " + pageNumber);
            }
            document.save(modifiedFile);
            logger.info("Modified File : " + modifiedFile, 1);
            logger.info(getElapsedTime(startTime));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }

    public void mergePDF(MultipartFile[] pdfFiles, String pdfPassword) {
        String mergedFileName = getAutoGeneatedMergedFileName();
        long startTime = System.currentTimeMillis();
        // Create a new PDF Merge Utility and set Destination and Information
        logger.info("Merging Files...", 1);
        PDFMergerUtility PDFmerger = new PDFMergerUtility();
        PDFmerger.setDestinationFileName(userAppDir.concat(File.separator).concat(mergedFileName));
        PDFmerger.setDestinationDocumentInformation(getDocumentInfo());

        // Add all the files
        try {
            for (MultipartFile pdfFile : pdfFiles) {
                PDFmerger.addSource(pdfFile.getInputStream());
                logger.info("Added file : " + pdfFile.getOriginalFilename());
            }

            // Merge the PDF documents
            PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            logger.info("Merged File : " + PDFmerger.getDestinationFileName());
            logger.info(getElapsedTime(startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Return autogenerated FileName
     *
     * @return Auto generated FileName
     */
    private String getAutoGeneatedMergedFileName() {
        return String.format("%s_%s.pdf", "Merged"
                , DateTimeFormatter.ofPattern("ddMMuuuu_hhmmss").format(LocalDateTime.now()));
    }

}
