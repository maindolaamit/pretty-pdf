package com.maindola.pdftools.prettypdf.controller;

import com.maindola.pdftools.prettypdf.exceptions.InvalidPDFException;
import com.maindola.pdftools.prettypdf.exceptions.InvalidPassword;
import com.maindola.pdftools.prettypdf.service.RestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RestApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestService service;
    RestApiController controller;

    @Test
    void encryptPDF_givenNullPassword_checkException() {
        MultipartFile pdfFile = null;
        String password = null;
        Exception excp = assertThrows(NullPointerException.class, () -> controller.encryptPDF(pdfFile, password,
                null));
    }

    @Test
    void encryptPDF_givenInvalidPassword_checkException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("user-file","Lecture 15 PCA_1.pdf",
                "application/pdf", "test data".getBytes());
        String password = "123";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/pretty-pdf/encrypt")
                .accept(MediaType.MULTIPART_FORM_DATA_VALUE).param("password",password);
        try {
            MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
            System.out.println(mvcResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        Exception excp = assertThrows(NullPointerException.class, () -> controller.encryptPDF(finalPdfFile, password,
//                ""));
//        assertEquals(excp.getMessage(), "Please enter a Password for PDF File.");
    }

    @Test
    void decryptPDF() {
    }

    @Test
    void splitPDF() {
    }

    @Test
    void deletePDF() {
    }

    @Test
    void mergePDF() {
    }
}