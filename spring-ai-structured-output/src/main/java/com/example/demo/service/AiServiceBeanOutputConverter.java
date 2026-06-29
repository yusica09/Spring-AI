package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Hotel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiServiceBeanOutputConverter {
	//## 필드 ##
	private ChatClient chatClient;
	
	//## 필드 ##
	public AiServiceBeanOutputConverter(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
	
	//## 메소드 ##
	public Hotel beanOutputConverterLowLevel(String city) {
		//구조화된 출력 변환기 생성
		BeanOutputConverter<Hotel> beanOutputConverter = new BeanOutputConverter<>(Hotel.class);
		//프롬프트 템플릿 생성
		PromptTemplate promptTemplate = PromptTemplate.builder()
				.template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
				.build();
		//템플릿 생성
		Prompt prompt = promptTemplate.create(Map.of(
				"city",city,
				"format",beanOutputConverter.getFormat())
				);
		//LLM의 JSON 출력 얻기
		String json = chatClient.prompt(prompt)
				.call()
				.content();
		//JSON을 Hotel로 매핑해서 변환
		Hotel hotel = beanOutputConverter.convert(json);
		return hotel;
	}
	
	public Hotel beanOutputConverterHighLevel(String city) {
		Hotel hotel = chatClient.prompt()
				.user("%s에서 유명한 호텔 목록 5개를 출력하세요.".formatted(city))
				.call()
				//BeanOutputConverter<Hotel>이 명시적으로 나타나있진 않지만
				//저수준 방식과 마찬가지로 entity() 메소드는 사용자 메시지 끝에 출력형식지침을 포함시킴
				.entity(Hotel.class);
		return hotel;
	}
	
	

}
