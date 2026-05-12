package com.cenimarket.backend.transaction.controller;

import com.cenimarket.backend.auth.domain.UserPrincipal;
import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.transaction.dto.request.TransactionCompleteRequest;
import com.cenimarket.backend.transaction.dto.response.TransactionCompleteResponse;
import com.cenimarket.backend.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionCompleteResponse>> createTransaction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TransactionCompleteRequest request
    ) {
        TransactionCompleteResponse response = transactionService.createTransaction(userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}