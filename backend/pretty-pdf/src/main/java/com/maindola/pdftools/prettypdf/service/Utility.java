package com.maindola.pdftools.prettypdf.service;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.util.Calendar;

public class Utility {

    /**
     * Print the Time elapsed
     *
     * @param startTime
     */
    public static String getElapsedTime(long startTime) {
        long elapsedTime = ((System.currentTimeMillis() - startTime) / 10000);
        return (String.format("Time taken : %d sec", elapsedTime));
    }

    /**
     * Check if a string is Null or Empty
     *
     * @param input Input String
     * @return Result
     */
    public static boolean isEmpty(String input) {
        if (input == null) return true;
        if (input.isEmpty() || input.length() == 0) return true;
        return false;
    }

    /**
     * Return the PDF Documentation Information
     *
     * @param documentInformation
     * @return PDDocumentInformation
     */
    public static PDDocumentInformation getDocumentInfo(PDDocumentInformation documentInformation) {
        String userName = System.getProperty("user.name");
        PDDocumentInformation info = new PDDocumentInformation();
        info.setAuthor(userName);
        info.setCreationDate(Calendar.getInstance());
        info.setCreator(documentInformation.getCreator());
        info.setTitle(String.format("%s-%s", documentInformation.getTitle(), "encrypted"));
        return info;
    }

    /**
     * Return the PDF Documentation Information
     *
     * @return PDDocumentInformation
     */
    public static PDDocumentInformation getDocumentInfo() {
        String userName = System.getProperty("user.name");
        PDDocumentInformation info = new PDDocumentInformation();
        info.setAuthor(userName);
        info.setCreationDate(Calendar.getInstance());
        info.setCreator(userName);
        info.setTitle("Merged Documents");

        return info;
    }

}
