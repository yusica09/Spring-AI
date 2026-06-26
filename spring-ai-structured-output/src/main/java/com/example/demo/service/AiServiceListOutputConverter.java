package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiServiceListOutputConverter {
	//## 필드 ##
	private ChatClient chatClient;

	//## 생성자 ##
	public AiServiceListOutputConverter(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
	
	//## 메소드 ##
	/* 
	 * 저수준 : 변환기를 직접 생성해서 형식 지침을 제공하고, 변환하는 방법
	 * 고수준 : ChatClient의 메소드 체이닝 맨 마지막에 entity() 메소드를 호출하는 방법
	 * 
	 * 코드의 간결성 -> 고수준
	 * 출력형식지침내용, LLM응답내용 직접 확인 및 처리 -> 저수준
	 * 
	 * */
	
	//저수준
	public List<String> listOutputConverterLowLevel(String city){
		//구조화된 출력 변환기 생성
		ListOutputConverter converter = new ListOutputConverter();
		//프롬프트 템플릿 생성
		PromptTemplate promptTemplate = PromptTemplate.builder()
				.template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
				.build();
		//프롬프트 생성
		Prompt prompt = promptTemplate.create(
				Map.of("city",city,"format",converter.getFormat()));
		//LLM의 쉼표로 구분된 텍스트 출력 얻기
		String commaSeparatedString = chatClient.prompt(prompt)
				.call()
				.content();
		//List<String>으로 변환
		List<String> hotelList = converter.convert(commaSeparatedString);
		
		return hotelList;
	}
	
	//고수준
	public List<String> listOutputConverterHighLevel(String city){
		List<String> hotelList = chatClient.prompt()
				.user("%s에서 유명한 호텔 목록 5개를 출력하세요.".formatted(city))
				.call()
				//entity() 메소드는 사용자 메시지 끝에 출력형식지침을 포함시킴
				.entity(new ListOutputConverter());
		
		return hotelList;
	}
	
	

}
