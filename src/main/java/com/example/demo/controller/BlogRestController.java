package com.example.demo.controller;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.domain.Article;
import com.example.demo.model.service.AddArticleRequest;
import com.example.demo.model.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController // @Controller + @ResponseBody
public class BlogRestController {
private final BlogService blogService;
@PostMapping("/api/articles")
public ResponseEntity<Article> addArticle(@ModelAttribute AddArticleRequest request) {
Article saveArticle = blogService.save(request);
return ResponseEntity.status(HttpStatus.CREATED)
.body(saveArticle);
}
}