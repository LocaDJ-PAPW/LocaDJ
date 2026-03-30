package com.LocaDj.controller.api;

import com.itextpdf.text.DocumentException;
import com.LocaDj.models.User;
import com.LocaDj.repositories.UserRepository;
import com.LocaDj.services.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class RelatorioApiController {

    private final UserRepository userRepository;
    private final PdfExportService pdfExportService;

    @GetMapping("/users-pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarPdf() {
        try {
            List<User> usuarios = userRepository.findAll();
            byte[] pdfBytes = pdfExportService.gerarRelatorioUsuarios(usuarios);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);


            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename("relatorio-usuarios.pdf")
                    .build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (DocumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}