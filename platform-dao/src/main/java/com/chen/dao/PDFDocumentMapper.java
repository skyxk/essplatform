package com.chen.dao;

import com.chen.entity.pdf.PDFDocument;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFDocumentMapper {
    PDFDocument findPDFDocumentByCode(String documentCode);
    int addPDFDocument(PDFDocument pdfDocument);
}
