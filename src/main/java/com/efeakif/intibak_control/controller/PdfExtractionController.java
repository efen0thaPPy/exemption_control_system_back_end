package com.efeakif.intibak_control.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.service.PdfExtractionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdfs")
@RequiredArgsConstructor
public class PdfExtractionController {

    private final PdfExtractionService pdfExtractionService;

    @PostMapping(value = "/extract-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractText(@RequestParam MultipartFile file) {
        try {

            if (file == null || file.isEmpty())
                return ResponseEntity.badRequest().body("File can't be empty");

            String text = pdfExtractionService.extractRawTextFromPdf(file);
            return ResponseEntity.ok(text);

        } catch (IOException ex) {

            return ResponseEntity.badRequest().body("Error parsing the pdf " + ex.getMessage());
        }

    }

}