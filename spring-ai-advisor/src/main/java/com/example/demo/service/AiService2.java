package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import com.example.demo.advisor.MaxCharLengthAdvisor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiService2 {
	// ##### 필드 #####
	private ChatClient chatClient;

	// ##### 생성자 #####
	public AiService2(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				// MaxCharLengthAdvisor를 최우선순위로 생성해서 기본 Advisor로 추가
				.defaultAdvisors(new MaxCharLengthAdvisor(Ordered.HIGHEST_PRECEDENCE))
				.build();
	}

	// ##### 메소드 #####
	public String advisorContext(String question) {
		String response = chatClient.prompt()
				.advisors(advisorSpec -> 
														// 공용 데이터에 키 상수로 100 저장
					advisorSpec.param(MaxCharLengthAdvisor.MAX_CHAR_LENGH, 100))
				.user(question)
				.call()
				.content();
		return response;
	} 
}
