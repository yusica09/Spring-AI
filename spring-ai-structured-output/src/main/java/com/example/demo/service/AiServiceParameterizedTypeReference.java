package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Hotel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiServiceParameterizedTypeReference {
	//## 필드 ##
	private ChatClient chatClient;

	//## 생성자 ##
	public AiServiceParameterizedTypeReference(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
	
	//## 메소드 ##
	public List<Hotel> genericBeanOutputConverterLowLevel(String cities){
		//구조화된 출력 변환기 생성
		BeanOutputConverter<List<Hotel>> beanOutputConverter = 
				new BeanOutputConverter<>(
						/*일반적으로 List<Hotel>같은 제네릭 타입은 
						  컴파일 이후 런타임 시에는 List만 남고 Hotel 정보는 사라짐.
						  런타임시 Hotel의 정보를 출력형식지침에 포함시키기 위해 아래의 익명객체 사용 */
						new ParameterizedTypeReference<List<Hotel>>(){}
						);
		//프롬프트 템플릿 생성
		PromptTemplate promptTemplate = new PromptTemplate(
				"""
				다음 도시들에서 유명한 호텔 3개를 출력하세요.
				{cities}
				{format}
				""");
		//프롬프트 생성
		Prompt prompt = promptTemplate.create(
				Map.of("cities",cities,
						"format",beanOutputConverter.getFormat()));
		//LLM의 JSON 출력 얻기
		String json = chatClient.prompt(prompt)
				.call()
				.content();
		//JSON을 List<Hotel>로 매핑해서 변환
		List<Hotel> hotelList = beanOutputConverter.convert(json);
		return hotelList;
	}
	
	public List<Hotel> genericBeanOutputConverterHighLevel(String cities){
		List<Hotel> hotelList = chatClient.prompt()
				.user("""
						다음 도시들에서 유명한 호텔 3개를 출력하세요.
						%s
						""".formatted(cities))
				.call()
				/*
				 * 코드에서는 BeanOutputConverter<List<Hotel>>이 명시적으로 드러나지는 않지만,
				 * 저수준 방식과 마찬가지로 entity() 메소드는 사용자 메시지 끝에 출력형식지침을 포함시킴.
				 * 또한 LLM의 JSON 출력을 List<Hotel>로 변환하고 반환함
				 */
				.entity(new ParameterizedTypeReference<List<Hotel>>() {});
		return hotelList;
	}

}
