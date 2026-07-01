package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ReviewClassification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiServiceSystemMessage {
	/*
	 * LLM에게 지시할 내용은 일반적으로 시스템 메시지에 포함시키지만, 
	 * entity()는 사용자 메시지에 출력형식지침을 포함시킴.
	 * 
	 * LLM의 출력형식지침을 두 메시지에 모두 포함시킬 수 있는데,
	 *   서술식 설명과 예시는 시스템 메시지에서 1차 지침 
	 *   entity()로 구체적인 타입 정보를 제공해서 좀 더 정확한 JSON을 출력하도록 2차지침
	 *    => 구조화된 출력기능 강력해짐
	 */
	
	//## 필드 ##
	private ChatClient chatClient;

	//## 생성자 ##
	public AiServiceSystemMessage(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}

	//## 메소드 ##
	public ReviewClassification classifyReview(String review) {
		ReviewClassification reviewClassification = chatClient.prompt()
				//1차 출력 지침
				.system("""
						영화 리뷰를 [POSITIVE, NEUTRAL, NEGATIVE] 중에서 하나로 분류하고,
						유효한 JSON을 반환하세요.
						""")
				.user("%s".formatted(review))
				.options(ChatOptions.builder()
						.temperature(0.0)
						.build())
				.call()
				//2차 출력 지침
				.entity(ReviewClassification.class);
		
		return reviewClassification;
	}
	
	
}
