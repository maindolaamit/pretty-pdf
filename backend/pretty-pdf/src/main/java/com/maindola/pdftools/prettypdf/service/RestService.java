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
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
     * @return New PDF file path
     */
    public Path encryptPDF(MultipartFile pdfFile, String password, String pdfPassword) {
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
            return Paths.get(encryptedFile);
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
     * @return New PDF file path
     */
    public Path decryptPDF(MultipartFile pdfFile, String password) {
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
                logger.info("Decrypted File : " + decryptedFile);
                logger.info(getElapsedTime(startTime));
                return Paths.get(decryptedFile);
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
     * Zip the contents of a Directory
     *
     * @param srcDir Directory Name
     * @return zip file
     * @throws IOException
     */
    private File zipDirectory(File srcDir) throws IOException {
        logger.info("Zipping directory and its content : " + srcDir.getAbsolutePath());
        byte[] buffer = new byte[1024];
        String zipFileName = srcDir.getAbsolutePath().concat(".zip");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));
        logger.info("Zipping Directory : " + zipFileName);
        for (File file : srcDir.listFiles()) {
            zos.putNextEntry(new ZipEntry(file.getName()));
            try (FileInputStream in = new FileInputStream(file)) {
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }
            logger.info("Added file to zip : " + file.getName());
            zos.closeEntry();
        }
        zos.close();
        return new File(zipFileName);
    }

    /**
     * Split the input PDF file into multiple pages based on given Page Numbers list
     *  @param pdfFile     PDF File to split
     * @param pageNumbers Page Number range to split PDF File into
     * @param pdfPassword PDF File password if any, Optional
     * @return
     */
    public Path splitPDF(MultipartFile pdfFile, List<Integer> pageNumbers, String pdfPassword) {
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

        // Zip the Directory and its contents
        try {
            File zipFile = zipDirectory(new File(splitFilesDir));
            // Delete the downloaded folder
            logger.info("Deleting folder ...");
            FileUtils.deleteDirectory(new File(splitFilesDir));
            return Paths.get(zipFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Delete single/multiple  pages from a PDF file
     *
     * @param pdfFile         PDF file
     * @param pageNumbersList Page number in comma delimited for multiple pages.
     * @param pdfPassword     PDF Password if any
     * @return New PDF file path
     */
    public Path deletePDF(MultipartFile pdfFile, List<Integer> pageNumbersList, String pdfPassword) {
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
            return Paths.get(modifiedFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }

    /**
     * Merge multiple files into single PDF file
     *
     * @param pdfFiles    PDF files to merge
     * @param pdfPassword PDF password if any
     * @return Path of new Merged PDF File
     */
    public Path mergePDF(MultipartFile[] pdfFiles, String pdfPassword) {
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
            return Paths.get(PDFmerger.getDestinationFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
