package com.cenimarket.backend.chat.controller;

import com.cenimarket.backend.auth.dto.request.LoginRequestDTO;
import com.cenimarket.backend.auth.dto.response.LoginResponseDTO;
import com.cenimarket.backend.auth.service.LoginService;
import com.cenimarket.backend.chat.dto.request.TestListingCreateRequest;
import com.cenimarket.backend.chat.service.TestService;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TestController {
    private final LoginService loginService;
    private final TestService testService;

    public TestController(LoginService loginService, TestService testService) {
        this.loginService = loginService;
        this.testService = testService;
    }


    @GetMapping("/index")
    public String home(){
        return "index";
    }

    @GetMapping("/test/loginpage")
    public String loginPage(){
        return "loginpage";
    }

    @PostMapping("/test/loginprocess")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO response = loginService.login(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/listing")
    public String listingPage(Model model){
        model.addAttribute("listings", testService.getListingList());
        return "listing";
    }

    //게시글 조회
    @GetMapping("/test/listing/{listingId}")
    public String showlistingDetail(@PathVariable Long listingId, Model model) {
        Listing list = testService.getListing(listingId).orElseThrow();
        model.addAttribute("listing", list);
        return "listingdetail";
    }

    //게시글 작성 페이지로
    @GetMapping("/test/listing/write")
    public String listingWritePage(){
        return "write";
    }


    //게시글 생성
    @PostMapping("/test/listing/new")
    public String createListing(@ModelAttribute TestListingCreateRequest request) {
        testService.createListing(request);
        return "redirect:/test/listing";
    }

}