package com.example.demo.controller;

import java.io.IOException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public String handleIOException(Model model, Exception e) {
        model.addAttribute("error", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        return "error_page"; // 파일 업로드 오류 처리 페이지
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Model model, Exception e) {
        model.addAttribute("error", "알 수 없는 오류가 발생했습니다: " + e.getMessage());
        return "error_page"; // 일반 오류 처리 페이지
    }
}
