package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import com.example.demo.model.domain.Article;
import com.example.demo.model.domain.Board;
import com.example.demo.model.service.AddArticleRequest;
//import com.example.demo.model.service.AddArticleRequest;
import com.example.demo.model.service.BlogService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BlogController {
    @Autowired
    BlogService blogService;
    
    // @GetMapping("/article_list") // 게시판 링크 지정
    // public String article_list(Model model) {
    // List<Board> list = blogService.findAll(); // 게시판 리스트
    // model.addAttribute("articles", list); // 모델에 추가
    // return "article_list"; // HTML 연결
    // }

    @GetMapping("/board_list") // 새로운 게시판 링크 지정
    public String board_list(Model model, 
                    @RequestParam(defaultValue = "0") int page, 
                    @RequestParam(defaultValue = "") String keyword,
                    HttpSession session) {//세션 객체 전달
                        // 세션 ID 가져오기
    String sessionId = session.getId(); 
    System.out.println("세션 ID: " + sessionId); // 터미널에 세션 ID 출력

        String userId = (String) session.getAttribute("userId"); // 세션 아이디 존재 확인
        String email = (String) session.getAttribute("email"); // 세션에서 이메일 확인

        if(userId == null){
            System.out.println("세션 userId가 없습니다. 로그인 페이지로 리다이렉션됩니다.");
            return "redirect:/member_login"; // 로그인 페이지로 리다이렉션
        } 
        System.out.println("세션 userID: " + userId);//서버 IDE 터미널에 세션 값 출력   
        
        if(email == null){
            System.out.println("세션 email이 없습니다. 로그인 페이지로 리다이렉션됩니다.");
            return "redirect:/member_login"; // 로그인 페이지로 리다이렉션
        } 
        System.out.println("세션 email: " + userId);//서버 IDE 터미널에 세션 값 출력 

        //페이지 처리 로직
    PageRequest pageable = PageRequest.of(page, 3); // 한 페이지의 게시글 수

    Page<Board> list; // Page를 반환
    if (keyword.isEmpty()) {
        list = blogService.findAll(pageable); // 기본 전체 출력(키워드 x)
    } else {
        list = blogService.searchByKeyword(keyword, pageable); // 키워드로 검색
    }
    int startNum = (page * pageable.getPageSize()) + 1; // 시작 번호 계산

    model.addAttribute("startNum", startNum); // 시작 번호 추가
    model.addAttribute("boards", list.getContent()); // 모델에 추가
    model.addAttribute("totalPages", list.getTotalPages()); // 페이지 크기
    model.addAttribute("currentPage", page); // 페이지 번호
    model.addAttribute("keyword", keyword); // 키워드
    model.addAttribute("email", email); // 로그인 사용자(이메일)

    return "board_list"; // .HTML 연결
    }

    @GetMapping("/board_view/{id}")
    public String board_view(Model model, @PathVariable Long id, HttpSession session) {
    Optional<Board> board = blogService.findById(id);
    String currentUser = (String) session.getAttribute("email");

    if (board.isPresent()) {
        model.addAttribute("board", board.get()); // 단일 객체 전달
        model.addAttribute("boards", board.get()); // 단일 객체 전달
        model.addAttribute("currentUser", currentUser);
    } else {
        return "/error_page/article_error";
    }
    return "board_view";
}




    @GetMapping("/board_edit/{id}") // 게시판 링크 지정
    public String article_edit(Model model, @PathVariable String id) {

        Long articleId = Long.parseLong(id); // 6주차 연습문제
        Optional<Board> list = blogService.findById(articleId); // 선택한 게시판 글
        List<Board> boards = blogService.findAll(); // 모든 게시글 조회

        if (list.isPresent()) {
            model.addAttribute("board", list.get()); // 존재하면 Article 객체를 모델에 추가
            model.addAttribute("boards", boards); // 모든 게시글 목록을 모델에 추가
        } else {
            // 처리할 로직 추가 (예: 오류 페이지로 리다이렉트, 예외 처리 등)
            return "/error_page/article_error"; // 오류 처리 페이지로 연결(이름 수정됨)
        }
        return "board_edit";
    }

    @GetMapping("/board_write")
    public String board_write(Model model, HttpSession session) {
    String email = (String) session.getAttribute("email");
    if (email == null) {
        email = "GUEST";
    }
    // 현재 날짜 계산
    LocalDate today = LocalDate.now();
    String formattedDate = today.format(DateTimeFormatter.ofPattern("MM월 dd일")); // 원하는 포맷으로 변경 가능
    model.addAttribute("email", email);
    model.addAttribute("newdate", formattedDate); // 현재 날짜를 모델에 추가
    return "board_write";
}

    

    @PutMapping("/api/board_edit/{id}")
    public String updateArticle(@PathVariable Long id, @ModelAttribute AddArticleRequest request) {
        blogService.update(id, request);
        return "redirect:/board_list"; // 글 수정 이후 .html 연결
    }

    @DeleteMapping("/api/board_delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        blogService.delete(id);
        return "redirect:/board_list";
    }

    @PostMapping("/api/boards") // 글쓰기 게시판 저장
    public String addboards(@ModelAttribute AddArticleRequest request) {
    blogService.save(request);
    return "redirect:/board_list"; // .HTML 연결
    }

    // // 5주차 연습문제
    // @PostMapping("/api/articles")
    // public String addArticle(@ModelAttribute AddArticleRequest request) {
    //     blogService.save(request);
    //     return "redirect:/article_list";
    // }

    // @GetMapping("/article_edit/{id}") // 게시판 링크 지정
    // public String article_edit(Model model, @PathVariable Long id) {
    //     Optional<Board> list = blogService.findById(id); // 선택한 게시판 글

    //     if (list.isPresent()) {
    //         model.addAttribute("boards", list.get()); // 존재하면 Article 객체를 모델에 추가
    //     } else {
    //         // 처리할 로직 추가 (예: 오류 페이지로 리다이렉트, 예외 처리 등)
    //         return "/error_page/article_error"; // 오류 처리 페이지로 연결(이름 수정됨)
    //     }
    //     return "article_edit"; // .HTML 연결
    // }

    // @PutMapping("/api/article_edit/{id}")
    // public String updateArticle(@PathVariable Long id, @ModelAttribute AddArticleRequest request) {
    //     blogService.update(id, request);
    //     return "redirect:/article_list"; // 글 수정 이후 .html 연결
    // }

    // @DeleteMapping("/api/article_delete/{id}")
    // public String deleteArticle(@PathVariable Long id) {
    //     blogService.delete(id);
    //     return "redirect:/article_list";
    // }
}