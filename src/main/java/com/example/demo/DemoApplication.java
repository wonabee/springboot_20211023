package com.example.demo;

import org.springframework.boot.SpringApplication; // 스프링 핵심 클래스
import org.springframework.boot.autoconfigure.SpringBootApplication; // 자동 설정 기능 활성화

@SpringBootApplication // 에노테이션(스프링부트 app 명시, 하위 다양한 설정 자동 등록)
public class DemoApplication { // 클래스 이름4

	//메인 메서드(프로그램의 시작점)
	public static void main(String[] args) { 
		SpringApplication.run(DemoApplication.class, args); // run 메서드로 실행
	}

}