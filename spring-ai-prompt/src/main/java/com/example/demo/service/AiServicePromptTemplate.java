package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiServicePromptTemplate {
	//##필드##
	private ChatClient chatClient;
	
	//UserMessage 프롬프트
	private PromptTemplate userTemplate = PromptTemplate.builder()
			.template("다음 한국어 문장을 {language}로 번역해 주세요.\n 문장: {statement}")
			.build();
	
	//SystemMessage + UserMessage 프롬프트
	private PromptTemplate systemTemplate = SystemPromptTemplate.builder()
			.template("""
					답변을 생성할 때 HTML과 CSS를 사용해서 파란 글자로 출력하세요.
					<span> 태그 안에 들어갈 내용만 출력하세요.
					""")
			.build();
	
	//##생성자##
	public AiServicePromptTemplate(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
	
	//##메소드##
	public Flux<String> promptTemplate1(String statement, String language){
		Prompt prompt = userTemplate.create(
				Map.of("statement", statement, "language", language));
		Flux<String> response = chatClient.prompt(prompt)
				.stream()
				.content();
		return response;
	}
	
	//각각의 프롬프트 템플릿에서 createMessage()를 호출해서 Massage객체 얻고 massage()에 제공
	public Flux<String> promptTemplate2(String statement, String language){
		Flux<String> response = chatClient.prompt()
				.messages(
						systemTemplate.createMessage(),
						userTemplate.createMessage(Map.of("statement", statement, "language", language)))
				.stream()
				.content();
		return response;
	}
	//각각의 프롬프트 템플릿에서 render()를 호출해서 메세지 텍스트를 얻고 system()과 user()에 각각 제공
	public Flux<String> promptTemplate3(String statement, String language){
		Flux<String> response = chatClient.prompt()
				.system(systemTemplate.render())
				.user(userTemplate.render(Map.of("statement", statement, "language", language)))
				.stream()
				.content();
		return response;
	}
	
	//재사용에 목적이 없고, 메소드 내에서 빠르게 데이터 바인딩해서 사용할 경우에 추천하는 방법
	public Flux<String> promptTemplate4(String statement, String language) {    
	    String systemText = """
	    		답변을 생성할 때 HTML와 CSS를 사용해서 파란 글자로 출력하세요.
	    		<span> 태그 안에 들어갈 내용만 출력하세요.
	    		""";
	    String userText = """
	    		다음 한국어 문장을 %s로 번역해주세요.\n 문장: %s
	    		""".formatted(language, statement); //매개변수화된 문자열 사용
	    
	    Flux<String> response = chatClient.prompt()
	    		.system(systemText)
	    		.user(userText)
	    		.stream()
	    		.content();
	    return response;
	  }     
	
}
