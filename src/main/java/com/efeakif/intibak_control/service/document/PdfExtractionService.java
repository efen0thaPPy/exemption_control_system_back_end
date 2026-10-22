package com.efeakif.intibak_control.service.document;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfExtractionService {

    public String extractRawTextFromMultipart(MultipartFile file) throws IOException {

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            stripper.setSortByPosition(true);

            return stripper.getText(document);

        }

    }

    public String extractRawTextFromResource(Resource resource) throws IOException {
        try (PDDocument document = Loader.loadPDF(resource.getContentAsByteArray())) {

            PDFTextStripper stripper = new PDFTextStripper();

            stripper.setSortByPosition(true);

            return stripper.getText(document);

        }

    }

}
