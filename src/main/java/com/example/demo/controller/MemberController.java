package com.example.demo.controller;

import com.example.demo.model.domain.Member; // 프로젝트의 Member 클래스
import com.example.demo.model.service.AddMemberRequest;
import com.example.demo.model.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model; // Spring의 Model 클래스
import org.springframework.validation.BindingResult;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/join_new") // 회원 가입 페이지 연결
    public String join_new(Model model) {
        model.addAttribute("memberRequest", new AddMemberRequest());
        return "join_new"; // .HTML 연결
    }

    @PostMapping("/api/members") // 회원 가입 저장
    public String addMembers(@Valid @ModelAttribute("memberRequest") AddMemberRequest request,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "join_new";
        }
        memberService.saveMember(request);
        return "join_end"; // .HTML 연결
    }

    @GetMapping("/member_login") // 로그인 페이지 연결
    public String member_login() {
        return "login"; // .HTML 연결
    }

    @PostMapping("/api/login_check") // 아이디, 패스워드 로그인 체크
    public String checkMembers(
            @ModelAttribute AddMemberRequest request,
            Model model,
            HttpServletRequest request2,
            HttpServletResponse response) {
        try {
            // 기존 세션 가져오기(존재하지 않으면 null 반환)
            HttpSession session = request2.getSession(false);
            if (session != null) {
                // 기존 세션 무효화
                session.invalidate();

                // 기존 JSESSIONID 쿠키 삭제
                Cookie cookie = new Cookie("JSESSIONID", null);
                cookie.setPath("/"); // 쿠키 경로
                cookie.setMaxAge(0); // 쿠키 삭제(0으로 설정)
                response.addCookie(cookie); // 응답으로 쿠키 전달
            }

            // 새로운 세션 생성
            session = request2.getSession(true);

            // 로그인 검증
            Member member = memberService.loginCheck(request.getEmail(), request.getPassword());
            String sessionId = UUID.randomUUID().toString(); // 임의의 고유 ID로 세션 생성
            session.setAttribute("userId", sessionId); // 세션에 사용자 ID 저장
            session.setAttribute("email", member.getEmail()); // 세션에 이메일 저장
            model.addAttribute("member", member); // 회원 정보를 모델에 전달

            // 로그인 성공 후 리다이렉트
            return "redirect:/board_list";

        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 에러 메시지 처리
            model.addAttribute("error", e.getMessage());
            return "login"; // 로그인 실패 시 로그인 페이지로 리다이렉트
        }
    }

    @GetMapping("/api/logout")
    public String member_logout(Model model, HttpServletRequest request2, HttpServletResponse response) {
        try {
            HttpSession session = request2.getSession(false); // 기존 세션 가져오기
            if (session != null) {
                session.invalidate(); // 기존 세션 무효화
                Cookie cookie = new Cookie("JSESSIONID", null); // 쿠키 초기화
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            return "login"; // 로그아웃 후 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            System.out.println("Logout error: " + e.getMessage()); // Logger 대신 출력
            model.addAttribute("error", "로그아웃 실패: " + e.getMessage());
            return "login";
        }
    }
}
