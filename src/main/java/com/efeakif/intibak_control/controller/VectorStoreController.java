package com.efeakif.intibak_control.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.efeakif.intibak_control.service.ai.VectorStoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vector-store")
@RequiredArgsConstructor
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;

    @DeleteMapping("/truncate")
    public ResponseEntity<Void> truncateTable() {
        vectorStoreService.clearTable();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

}
