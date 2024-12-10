package com.example.demo.model.service;

import com.example.demo.model.domain.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequest {
    @NotBlank (message = "이름은 필수 입력 항목입니다.")
    private String name;

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank (message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank (message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank (message = "나이는 필수 입력 항목입니다.")
    private String age;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String mobile;

    @NotBlank (message = "주소는 필수 입력 항목입니다.")
    private String address;

    public Member toEntity() { // Member 생성자를 통해 객체 생성
        return Member.builder()
            .name(name)
            .email(email)
            .password(password)
            .age(age)
            .mobile(mobile)
            .address(address)
            .build();
    }
}